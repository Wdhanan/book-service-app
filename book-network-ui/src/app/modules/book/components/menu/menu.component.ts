import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {

  constructor(private router: Router){

  }

  // to show which element of my navbar is active even if i reload the page
  ngOnInit(): void {
    const linkColor = document.querySelectorAll('.nav-link'); // selecter all the element with the class "nav-link"
    linkColor.forEach( link => {
      // if my window location ends with "href" or is empty
      if(window.location.href.endsWith(link.getAttribute('href') ||  '' )){
        link.classList.add('active'); // make the element active

      }
      // the event which happens when someone  click on the navbar elements
      link.addEventListener('click', () => {
        linkColor.forEach( l =>  l.classList.remove('active'));// remove active on all the elements
        link.classList.add('active');// activate the current one which just got clicked
          
        

        

      } );

    });
  }

  logout():void{
    //clear the token or localstorage.clear()
    localStorage.removeItem('token');
    // reload the page
    window.location.reload();
    

  }

}
