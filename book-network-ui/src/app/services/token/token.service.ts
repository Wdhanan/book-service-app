import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  // save the token with the name "token" in the localStorage
  set token (token: string){
    localStorage.setItem('token', token);
  }
  get token (): string{
    
    return localStorage.getItem('token') as string;
  }
  isTokenNotValid(): boolean{
    return !this.isTokenValid();

  }

  isTokenValid():boolean{
    // get the token
    const token = this.token;
    if(!token){
      return false;
    }
    //decode the token
    const jwtHelper = new JwtHelperService(); // from the dependency we installed
    const isTokenExpired = jwtHelper.isTokenExpired(token);

    if (isTokenExpired){
      localStorage.clear();
      return false;
    }
    return  true;

  }
}
