package com.nounours.book.book;

import com.nounours.book.common.BaseEntity;
import com.nounours.book.feedback.Feedback;
import com.nounours.book.history.BookTransactionHistory;
import com.nounours.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder // because of inheritance
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Book extends BaseEntity { // inheritance

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;// to store book images. they are not going to be stored inside the database because it could be so much consuming
    private  boolean archived;
    private boolean shareable;
    @ManyToOne
    @JoinColumn(name= "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    @Transient
    //Method to calculate the Rate
    public double getRate(){
        if ( feedbacks == null || feedbacks.isEmpty()){
            return  0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        // make 3.23 --> 3.0 || 3.54 --> 4

        return (Math.round(rate * 10.0) / 10.0);

    }

}
