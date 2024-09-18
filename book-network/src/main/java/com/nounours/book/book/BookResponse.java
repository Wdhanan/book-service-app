package com.nounours.book.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse { // responseBody from the database

    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String owner;// username  not the id
    private byte [] cover; // to store images
    private double rate; // average of the feedbacks divided by the number of the feedback
    private boolean archived;
    private boolean shareable;

}
