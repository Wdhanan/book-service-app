package com.nounours.book.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder // instead of @Builder because of inheritance
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass // must be there to make sure of the inheritance of attribute. It means that it is the base class
@EntityListeners(AuditingEntityListener.class)
// base class which has some attributes that are  being inherited from other entities ( Table in the database)
public class BaseEntity {

    @Id
    @GeneratedValue
    private Integer id;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(insertable = false) // we do not want to create it automatically once we create a new line
    private LocalDateTime lastmodifiedDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Integer createdBy; // because our "User-id" is from type "Integer"

    @LastModifiedBy
    @Column(insertable = false)
    private Integer lastmodifiedBy;
}
