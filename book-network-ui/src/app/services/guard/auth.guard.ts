import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../token/token.service';

export const authGuard: CanActivateFn = () => { // for this case we dont need the parameters (route, state) 

  // inject the tokenservice
  const tokenservice = inject(TokenService); // we use inject() because we can not have the constructor here
  const router = inject(Router);
  // if the token is not valid we navigate the user to the login page
  if( tokenservice.isTokenNotValid() ){

    router.navigate(['login']);

    return false; // the guard return a boolean value

  }
  return true;
};
