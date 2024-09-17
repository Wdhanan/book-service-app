package com.nounours.book.feedback;

import com.nounours.book.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name ="Feedback")

public class FeedbackController {
    @Autowired
    private final FeedbackService service;
    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    //get all the feedback from a book. These can be a lot that is why we use PageResponse
    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
     @PathVariable("book-id") Integer bookId,
     @RequestParam(name= "page", defaultValue = "0", required = false) int page,
     @RequestParam(name= "size", defaultValue = "10", required = false) int size,
     Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllFeedbacksByBook(bookId, page, size, connectedUser));
    }

}
