package com.nounours.book.config;

import com.nounours.book.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// need to implement the "AuditorAware" to tell spring who is doing what
public class ApplicationAuditAware implements AuditorAware<Integer> {
    @Override
    public Optional<Integer> getCurrentAuditor() {
        // get the current Auditor mit Authentication from Spring
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null ||
        !authentication.isAuthenticated() ||
        authentication instanceof AnonymousAuthenticationToken){

            return Optional.empty();

        }
        User userPrincipal = (User) authentication.getPrincipal(); // get the User from the "Principal" the User is implementing

        return Optional.ofNullable(userPrincipal.getId());
    }
}
