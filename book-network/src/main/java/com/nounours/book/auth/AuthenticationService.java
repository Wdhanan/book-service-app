package com.nounours.book.auth;

import com.nounours.book.email.EmailService;
import com.nounours.book.email.EmailTemplateName;
import com.nounours.book.security.JwtService;
import com.nounours.book.role.RoleRepository;
import com.nounours.book.user.Token;
import com.nounours.book.user.TokenRepository;
import com.nounours.book.user.User;
import com.nounours.book.user.UserRepository;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TokenRepository tokenRepository;
    @Autowired
    private final EmailService emailService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    @Autowired
    private final AuthenticationManager authenticationManager; // from Spring, we need to create the method of it in the class "Beansconfig"
    @Autowired
    private final JwtService jwtService;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER") // default role
        //todo - better exception handling
        .orElseThrow(()-> new IllegalArgumentException("ROLE USER was not initialized"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))// the password is encoded while it is saved in the database
                .accountLocked(false)
                .enabled(false) // by default is the account not enabled
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);// save to the database
        //method to send the Validation email
        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {

        var newToken = generateAndSaveActivationToken(user);
        //send email
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"

        );

    }

    private String generateAndSaveActivationToken(User user) {

        //generate a Token
        String generatedToken = generateActivationCode(6);// the length of the token is "6"
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // expires after 15 Minutes
                .user(user)
                .build();
        tokenRepository.save(token); // save the Token to the database

        return  generatedToken;


    }

    // generate a code of six digit
    private String generateActivationCode(int length) {// generate a token based on a length
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom(); // secure the token

        for (int i= 0; i < length; i++){
            int randomIndex = secureRandom.nextInt(characters.length()); // from 0 to 9
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var auth = authenticationManager.authenticate( // This method is direct available from spring
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        //get user from authentication
        var user = ((User)auth.getPrincipal());
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder().token(jwtToken).build();

    }

    //@Transactional // because we retrieve the token from the database
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                //todo better exception handling
                .orElseThrow(() -> new RuntimeException("Invalid Token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){ // check if the Token is already expired
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email adress.");
        }
        // token not expired
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        //enable the user
        user.setEnabled(true);
        userRepository.save(user);
        //validate token
        savedToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(savedToken); // update the token that has been validated
    }
}
