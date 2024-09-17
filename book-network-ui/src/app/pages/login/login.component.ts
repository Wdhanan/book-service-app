import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationRequest } from 'src/app/services/models';
import { AuthenticationService } from 'src/app/services/services';
import { TokenService } from 'src/app/services/token/token.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  // to store the credentials im json format
  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsg: Array<string> = [];// save the erros from backend
  constructor(
    private router:Router,
    private authService: AuthenticationService,
    // inject another service(token service to set and get the Token after login)
    private tokenService: TokenService

  ){

  }

  login (): void{
    // reset the errorMsg to null
    this.errorMsg  = [];
    this.authService.authenticate({
      body: this.authRequest

    }).subscribe({ // subscribe methode to get a response from the backend

      next: (res) => {
        // todo save the token
        this.tokenService.token= res.token as string; // cast to string
        // navigate the user to the Books page
        this.router.navigate(['books']);

      },
      error:(err)=>{
        console.log(err);
        // display the errors on our loginForm
        if(err.error.validationErrors){ // "validationErrors" from our Backend
          this.errorMsg = err.error.validationErrors;
        } else{
          this.errorMsg.push(err.error.error);
        }
        
      }

    });

  }
  register():void{
    // navigate the user to the register page. we need a construtor for it
    this.router.navigate(['register']);

  }

}
