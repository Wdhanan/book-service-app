import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/services';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss']
})
export class ActivateAccountComponent {
  code: string[] = ['', '', '', '', '', '']; // array to get all the number given from the user
  message: string = '';
  isOkay:boolean = true; // check if the validation code isok or not
  submitted: boolean = false; // check if the user has already submitted the code or not

  constructor(
    private router:Router,
    private authService: AuthenticationService

  ){

  }

  moveToNextInput(event: KeyboardEvent, nextIndex: number) {
    if ((event.target as HTMLInputElement).value.length === 1 && nextIndex < this.code.length) {
      const nextInput = document.querySelectorAll('input')[nextIndex] as HTMLInputElement;
      nextInput.focus();
    }
    this.checkCodeCompletion();
  }
  // check if all the fields were setted

  checkCodeCompletion() {
    if (this.code.every(c => c.length === 1)) { // only if all the fields were setted, then the method "onCodeCompleted" get called
      this.onCodeCompleted(this.code.join('')); // join the code and call the method
    }
  }

  // handle the  full validation code to tell the user if it is correct or not
  onCodeCompleted(token: string): void {
    // Handle the completed code here
    console.log('Code completed:', token);
    this.confirmAccount(token);
  }
  // get the confirmation of  account from our Backend
  private confirmAccount(token:string) {
    this.authService.confirm({
      token
    }).subscribe({
      next: () => {
        this.message = 'Your account has been successfully activated.\n Now you can proceed to login';
        this.submitted = true; // submission worked
        this.isOkay = true;

      },
      error: () =>{
        this.message = 'Token has been expired or invalid';
        this.submitted = true; // submission worked but isOkay = false
        this.isOkay = false;

      }
    });
  }

  redirectToLogin():void{
     // navigate the user to the login page
     this.router.navigate(['login']);

  }

  tryAgain(): void {
    this.code = ['', '', '', '', '', '']; // Clear the code array
    this.submitted = false; // Reset submission state
    setTimeout(() => {
      // Focus the first input field
      (document.querySelector('input') as HTMLInputElement).focus();
    }, 0);
  }

}


