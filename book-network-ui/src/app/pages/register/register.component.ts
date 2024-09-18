import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegistrationRequest } from 'src/app/services/models';
import { AuthenticationService } from 'src/app/services/services';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerRequest: RegistrationRequest = {email: '', firstname:'', lastname:'', password:''};
  errorMsg: Array<string> = [];

  constructor(private router:Router ,
    private authService: AuthenticationService
  ){
    

  }



  register():void{
    // reset the errorMsg to null
    this.errorMsg  = [];
    this.authService.register({
      body: this.registerRequest

    }).subscribe({ // subscribe methode to get a response from the backend

      next: (res) => {
        
        // navigate the user to the activate-account page
        this.router.navigate(['activate-account']);

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

  login(): void{
    // navigate the user to the login page
    this.router.navigate(['login']);

  }

}
