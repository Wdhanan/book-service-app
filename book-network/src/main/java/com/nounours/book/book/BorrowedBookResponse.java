package com.nounours.book.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {
    private Integer id;
    private String title;
    private String authorName;
    private String isbn;

    private double rate; // average of the feedbacks divided by the number of the feedback
    private boolean returned;
    private boolean returnApproved;
}
