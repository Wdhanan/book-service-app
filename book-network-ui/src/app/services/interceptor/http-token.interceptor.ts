import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { InjectSetupWrapper } from '@angular/core/testing';
import { Observable, retry } from 'rxjs';
import { TokenService } from '../token/token.service';

@Injectable() // the object can be injected
export class httpTokenInterceptor implements HttpInterceptor {

  constructor(private tokenService: TokenService){

  }


  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token:string = this.tokenService.token;
    if(token){
      // if the token exists , we set our authorization token to connect with the backend
      const authRequest = request.clone(  {
        headers: new HttpHeaders({ // we need to set the authorization header in order to communication with our backend
          Authorization: 'Bearer ' + token
        })
      });

      return next.handle(authRequest); // token exists

    }
    
    return next.handle(request);
  }
  
}
