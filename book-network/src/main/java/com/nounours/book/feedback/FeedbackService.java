package com.nounours.book.feedback;

import com.nounours.book.book.Book;
import com.nounours.book.book.BookRepository;
import com.nounours.book.common.PageResponse;
import com.nounours.book.exception.OperationNotPermittedException;
import com.nounours.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    @Autowired
    private final BookRepository bookRepository;
    @Autowired
    private final FeedbackMapper feedbackMapper;
    @Autowired
    private final FeedbackRepository feedbackRepository;
    public Integer save(FeedbackRequest request, Authentication connectedUser) {

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " +request.bookId()));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You can not give a feedback for an archived or not shareable book.");
        }
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        //check that the user is not  the owner of the book
        if (Objects.equals(book.getOwner().getId(), user.getId())){
            //throw exception
            throw new OperationNotPermittedException("You can not give a feedback to your own book.");

        }
        Feedback feedback = feedbackMapper.toFeedback(request);

        return feedbackRepository.save(feedback).getId();

    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        // get the user from authentication Object
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedbackRepository.findAllBookByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses =feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return  new PageResponse<>(feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast());
    }
}
