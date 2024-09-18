import { Component, OnInit } from '@angular/core';
import { PageResponseBorrowedBookResponse, BorrowedBookResponse, FeedbackRequest } from 'src/app/services/models';
import { BookService, FeedbackService } from 'src/app/services/services';

@Component({
  selector: 'app-return-books',
  templateUrl: './return-books.component.html',
  styleUrl: './return-books.component.scss'
})
export class ReturnBooksComponent implements OnInit {

  constructor
  (private bookService:BookService
  ){

  }

  

  ngOnInit(): void {
    this.findAllReturnedBooks();
   
  }

  returnedBooks: PageResponseBorrowedBookResponse = {};
  page = 0;
  size = 5;
  message:string = '';
  level = "sucess";
    

  findAllReturnedBooks(): void{
    this.bookService.findAllReturnedBooks({
      page:this.page,
      size: this.size
    }).subscribe({
      next: (response) =>{
        this.returnedBooks = response;

      }
    })

  }


  goToFirstPage():void{
    this.page = 0;
    this.findAllReturnedBooks();

  }

  goToPreviousPage():void{
    this.page--;
    this.findAllReturnedBooks();

  }

  goToPage(pageIndex:number):void{
    this.page = pageIndex;
    this.findAllReturnedBooks();

  }

  goToNextPage():void{
    this.page++;
    this.findAllReturnedBooks();

  }
  goToLastPage():void{
    this.page = this.returnedBooks.totalPages as number - 1 ;
    this.findAllReturnedBooks();

  }

  get isLastPage(): boolean{

    return this.page == this.returnedBooks.totalPages as number - 1;
  }

  approveBookReturn(book: BorrowedBookResponse ):  void{

    if(!book.returned){
      this.level = "error";
      this.message = 'The Book is not yet returned';
      return;
    }
    this.bookService.approveReturnBorrowedBook({
      'book-id': book.id as number
    }).subscribe({
      next: () =>{
        this.level = "sucess";
        this.message = 'Book return approved';
        this.findAllReturnedBooks();

      }
    });

  }

}
