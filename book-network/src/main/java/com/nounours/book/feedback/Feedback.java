package com.nounours.book.feedback;

import com.nounours.book.book.Book;
import com.nounours.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder // because of inheritance
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Feedback extends BaseEntity {


    private Double note;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;



}
