import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BookResponse, PageResponseBookResponse } from 'src/app/services/models';
import { BookService } from 'src/app/services/services';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {

  bookResponse: PageResponseBookResponse = {};
  page = 0;
  size = 4; // display only four books per page ; in the backend the default is 10; but for the frontend we just want 5
  message:string = ''; 
  level = "sucess";
  

  constructor(private bookService: BookService,
    private router: Router){
    

  }
 

  // ngOnInit() to load our infos (our books) directly from the beginning when the page loads
  ngOnInit(): void {
    // find all the books
    this.findAllBooks();
  }


  findAllBooks(): void {

    this.bookService.findAllBooks({page: this.page,
      size: this.size} // call our backend method to find all our Books
      
    ).subscribe({
      next: (response) =>{
        this.bookResponse = response; // set our response to our Variable "bookResponse"

      }
    })
    
  }

  goToFirstPage():void{
    this.page = 0;
    this.findAllBooks();

  }

  goToPreviousPage():void{
    this.page--;
    this.findAllBooks();

  }

  goToPage(pageIndex:number):void{
    this.page = pageIndex;
    this.findAllBooks();

  }

  goToNextPage():void{
    this.page++;
    this.findAllBooks();

  }
  goToLastPage():void{
    this.page = this.bookResponse.totalPages as number - 1 ;
    this.findAllBooks();

  }

  get isLastPage(): boolean{

    return this.page == this.bookResponse.totalPages as number - 1;
  }

  borrowBook(book: BookResponse):void{
    this.message = '';
    this.bookService.borrowBook({
      'book-id': book.id as number
    }).subscribe({
      next: () =>{
        this.level= "success";
        this.message = 'Book successfully added to your list';

      },
      error: (err) =>{
        console.log(err);
        this.level = "error";
        this.message = err.error.error;

      }
    });

  }

}
