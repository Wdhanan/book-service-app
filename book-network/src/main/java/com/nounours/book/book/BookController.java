package com.nounours.book.book;

import com.nounours.book.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {
    @Autowired
    private final BookService service;

    @PostMapping
    public ResponseEntity<Integer>saveBook( @Valid @RequestBody BookRequest request,
                                           Authentication connectedUser) {// retrive the conneccted User with the Spring authentication Object
        return ResponseEntity.ok(service.save(request, connectedUser));

    }
    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Integer bookId){

        return  ResponseEntity.ok(service.findById(bookId));


    }

    // Get All the shareable books which are not from the current connected user
    @GetMapping("")
    // implement pagging to return a book at one
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name ="page", defaultValue = "0", required = false) int page,
            @RequestParam(name ="size", defaultValue = "10", required = false) int size,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    // getBook by Owner
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name ="page", defaultValue = "0", required = false) int page,
            @RequestParam(name ="size", defaultValue = "10", required = false) int size,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    // getBook by Owner
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name ="page", defaultValue = "0", required = false) int page,
            @RequestParam(name ="size", defaultValue = "10", required = false) int size,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, connectedUser));
    }

    @GetMapping("/returned")
    // getBook by Owner
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name ="page", defaultValue = "0", required = false) int page,
            @RequestParam(name ="size", defaultValue = "10", required = false) int size,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{book-id}") // == @PutMapping
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId, Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateShareableStatus(bookId, connectedUser));


    }

    @PatchMapping("/archived/{book-id}") // == @PutMapping
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId, Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, connectedUser));


    }

    //borrow a Book( who wants to borrow which book?
    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(@PathVariable("book-id") Integer bookId,
                                              Authentication connectedUser
    ){

    return  ResponseEntity.ok(service.borrowBook(bookId, connectedUser));
    }

    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnborrowBook(@PathVariable("book-id") Integer bookId,
                                              Authentication connectedUser
    )
    {
        return ResponseEntity.ok(service.returnborrowBook(bookId, connectedUser));
    }

    //approved returned borrowed Book
    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowedBook(@PathVariable("book-id") Integer bookId,
                                                    Authentication connectedUser
    )
    {
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, connectedUser));
    }

    //upload cover picture of a specified Book. we are going to save it to our server not the database
    @PostMapping(value ="/cover/{book-id}", consumes = "multipart/form-data") // "consumes ="multipart/form-data"" is important to be able to store file data
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter() // from Swagger
            @RequestPart("file") MultipartFile file, // for the image we use "MultipartFile"-Type
            Authentication connectedUser

    ){
        service.uploadBookCoverPicture(file, connectedUser, bookId);
        return ResponseEntity.accepted().build();

    }



}
