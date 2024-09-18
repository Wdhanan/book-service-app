import { Component, OnInit } from '@angular/core';
import { BorrowedBookResponse, FeedbackRequest, PageResponseBorrowedBookResponse } from 'src/app/services/models';
import { BookService, FeedbackService } from 'src/app/services/services';

@Component({
  selector: 'app-borrowed-book-list',
  templateUrl: './borrowed-book-list.component.html',
  styleUrl: './borrowed-book-list.component.scss'
})
export class BorrowedBookListComponent implements OnInit {
  constructor
  (private bookService:BookService,
    private feedbackService: FeedbackService
  ){

  }

  ngOnInit(): void {
    this.findAllBorrowedBooks();
   
  }

  borrowedBooks: PageResponseBorrowedBookResponse = {};
  page = 0;
  size = 5;
  selectedBook : BorrowedBookResponse | undefined; // if no book selected then nothing is shown
  feedbackRequest: FeedbackRequest = {
    bookId: 0,
    comment: '',
    note: 0
  }; // for our Form to save feedback in our Backend

  returnBorrowedBook(book : BorrowedBookResponse) : void{

    // Variable to keep track of the selected element
    this.selectedBook = book; 

    // set the book id
    this.feedbackRequest.bookId = book.id as number;

  }

  //
  returnBook(withFeedBack:boolean): void{
    this.bookService.returnborrowBook({
      'book-id': this.selectedBook?.id as number
    }).subscribe({
      next: () =>{
        // if we want to give with Feedback
        if(withFeedBack){
          this.giveFeedback();
        }
        this.selectedBook = undefined;
        this.findAllBorrowedBooks();
      }
    })

  }

  giveFeedback():void{
    this.feedbackService.saveFeedback({
      body:this.feedbackRequest
    }).subscribe({
      next : () =>{

      }
    });


  }

  findAllBorrowedBooks(): void{
    this.bookService.findAllBorrowedBooks({
      page:this.page,
      size: this.size
    }).subscribe({
      next: (response) =>{
        this.borrowedBooks = response;

      }
    })

  }


  goToFirstPage():void{
    this.page = 0;
    this.findAllBorrowedBooks();

  }

  goToPreviousPage():void{
    this.page--;
    this.findAllBorrowedBooks();

  }

  goToPage(pageIndex:number):void{
    this.page = pageIndex;
    this.findAllBorrowedBooks();

  }

  goToNextPage():void{
    this.page++;
    this.findAllBorrowedBooks();

  }
  goToLastPage():void{
    this.page = this.borrowedBooks.totalPages as number - 1 ;
    this.findAllBorrowedBooks();

  }

  get isLastPage(): boolean{

    return this.page == this.borrowedBooks.totalPages as number - 1;
  }

  

}
