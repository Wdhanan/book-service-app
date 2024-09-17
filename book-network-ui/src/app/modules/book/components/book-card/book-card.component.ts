import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BookResponse } from 'src/app/services/models';

@Component({
  selector: 'app-book-card',
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.scss'
})
export class BookCardComponent {
  // to input it inside the parent components/pages
  @Input() book: BookResponse = {};
  @Input() manage:boolean = false;
   

  get_book(): BookResponse{
    return this.book;

  }

  get_manage(): boolean{
    return this.manage;

  }

  set_manage(value: boolean): void{
    this.manage = value;

  }

  
  
  set_book(value:BookResponse): void {
    this.book = value;

  }
  // method to get the BookCover()
  get bookCover(): string | undefined {
    //do we have the book?
    if(this.book.cover){
      // to display a base64 image
      return 'data:image/jpg;base64, ' + this.book.cover;

    }
    // return a random picture from source.unsplash
    return 'https://source.unsplash.com/user/c_v_r/1900x800';
  }

  //to communicate an Event from the child(book-card) to the parent (book-list) for all our edit buttons
  //
  @Output() private share: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private archive: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private addToWaitingList: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private borrow: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private edit: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output() private details: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();

  onShowDetails():void{
    this.details.emit(this.book);

  }

  onBorrow():void{
    this.borrow.emit(this.book);

  }
  onAddToWaitingList():void{
    this.addToWaitingList.emit(this.book);

  }

  onEdit():void{

    this.edit.emit(this.book);

  }

  onShare():void {
    this.share.emit(this.book);

  }

  onArchive():void{
    this.archive.emit(this.book);

  }
 


}
