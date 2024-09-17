package com.nounours.book.book;

import com.nounours.book.common.PageResponse;
import com.nounours.book.exception.OperationNotPermittedException;
import com.nounours.book.file.FileStorageService;
import com.nounours.book.history.BookTransactionHistory;
import com.nounours.book.history.BookTransactionHistoryRepository;
import com.nounours.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.nounours.book.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
public class BookService {

    @Autowired
    private final BookMapper  bookMapper; // a service class(just a class with a method where the Book got setted up)
    @Autowired
    private final BookRepository bookRepository;
    @Autowired
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    @Autowired
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper:: toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID:: " +bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //implement paging from springframework
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending()); // take the page sorted by the createdDate in descdending modus
        // return only the books that are displayable . here we want all the books except from the connected one
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
              bookResponse,
              books.getNumber(),
              books.getSize(),
              books.getTotalElements(),
              books.getTotalPages(),
              books.isFirst(),
              books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //implement paging from springframework
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending()); // take the page sorted by the createdDate in descdending order
        // retrive all the Book with a spefication setted inside the class BookSpecification
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );

    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //implement paging from springframework
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );




    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //implement paging from springframework
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );

    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " +bookId));
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //make sure only the connected user make changes about the specified book
        if (!Objects.equals(book.getOwner().getId(), user.getId())){
            //throw exception
            throw new OperationNotPermittedException("You are not allowed to update shareable status of this book");

        }
        book.setShareable(!book.isShareable()); // invert the current status of the book
        bookRepository.save(book);

        return bookId;

    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " +bookId));
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //make sure only the connected user make changes about the specified book
        if (!Objects.equals(book.getOwner().getId(), user.getId())){
            //throw exception
            throw new OperationNotPermittedException("You are not allowed to update archived status of this book");

        }
        book.setArchived(!book.isArchived()); // invert the archived current status of the book
        bookRepository.save(book);

        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        //first check if the book is available in our database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID:: " +bookId));
        //check if the book is not archived and if it is also shareable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The requested book can not be borrowed since it is archived or not shareable.");

        }
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //check that the user is not  the owner of the book
        if (Objects.equals(book.getOwner().getId(), user.getId())){
            //throw exception
            throw new OperationNotPermittedException("You are already the owner of the book. you can not borrow your own book.");

        }
        //check if the book is already borrowed
        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if(isAlreadyBorrowed){
            throw new OperationNotPermittedException("The requested book is already borrowed.");

        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();// return the id of the transaction

    }

    public Integer returnborrowBook(Integer bookId, Authentication connectedUser) {
        //first check if the book is available in our database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID:: " +bookId));
        //check if the book is not archived and if it is also shareable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The requested book can not be borrowed since it is archived or not shareable.");

        }
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //check that the user is not  the owner of the book
        if (Objects.equals(book.getOwner().getId(), user.getId())){
            //throw exception
            throw new OperationNotPermittedException("You can not borrow or return your own book.");

        }

        // make sure that the user has already borrowed his book
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("you did not borrow this book:: " + bookId));

        // once all the checks are ok we just need to set the returned status of the book to true
        bookTransactionHistory.setReturned(true);

        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();




    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {

        //first check if the book is available in our database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID:: " +bookId));
        //check if the book is not archived and if it is also shareable
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The requested book can not be borrowed since it is archived or not shareable.");

        }
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //check that the user is not  the owner of the book
        if (!Objects.equals(book.getOwner().getId(), user.getId())){
            //throw exception
            throw new OperationNotPermittedException("You can not return a book that you do not own.");

        }
        // make sure that the user has already borrowed his book
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You can not approve is returned"));
        bookTransactionHistory.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();




    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        // create a FileStorageService as helper to store the file

        //first check if the book is available in our database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID:: " +bookId));
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());

        var bookCover = fileStorageService.saveFile(file, user.getId()); // for each user a folder is created to store all the images which belong to him
        book.setBookCover(bookCover); // BookCover is from Type "String" that means the file path of the cover pictures
        bookRepository.save(book);// save the changes made on the book in our database






    }
}
