import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookRequest } from 'src/app/services/models';
import { BookService } from 'src/app/services/services';

@Component({
  selector: 'app-manage-book',
  templateUrl: './manage-book.component.html',
  styleUrl: './manage-book.component.scss'
})
export class ManageBookComponent implements OnInit {

  constructor(
    private bookService: BookService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ){

  }

  // what happens when we load this component to set the element from the backend in our update form
  ngOnInit(): void {
    const bookId =this.activatedRoute.snapshot.params['bookId'];
    if(bookId){
      this.bookService.findBookById({
        'book-id':bookId
      }).subscribe({
        next:(book) =>{
          this.bookRequest = {
            id: book.id,
            title: book.title as string,
            authorName: book.authorName as string,
            isdn: book.isbn as string,
            synopsis: book.synopsis as string,
            shareable: book.shareable

          }

          //also set the book cover from the backend if it exists 
          if(book.cover){
            this.selectedPicture = 'data:image/jpg;base64,' + book.cover; 
          }

        }
      })
    }
    
  }

  bookRequest: BookRequest = {
    authorName: '',
    isdn: '',
    synopsis: '',
    title: ''
  };
  
  errorMsg: Array<string> = [];
  selectedPicture: string | undefined;
  selectedBookCover:any;
  display: boolean =false;


  onFileSelected(event: any):void {
    //get a file from the selected file
    this.selectedBookCover = event.target.files[0]; // the first element
    console.log(this.selectedBookCover);

    if(this.selectedBookCover){
      const reader = new FileReader();
      // wait for the loading of the picture
      reader.onload = () => {
        //result of the loading
        this.selectedPicture = reader.result as string; 

      }
      reader.readAsDataURL(this.selectedBookCover);
    }



  }

  saveBook(): void{

    this.bookService.saveBook({
      body: this.bookRequest
    }).subscribe({

      next: (bookId) => {

        this.bookService.uploadBookCoverPicture({
          'book-id':bookId,
          body: {
            file: this.selectedBookCover
          }


        }).subscribe ({
          next:() =>{
            this.router.navigate(['/books/my-books']);

          }
        })

      },

      error: (err) =>{
        this.display = true;

        this.errorMsg = err.error.validationErrors;

      }

    });

  }

}
