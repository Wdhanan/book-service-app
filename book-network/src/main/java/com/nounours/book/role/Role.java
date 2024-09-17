package com.nounours.book.role;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nounours.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // you must add "@EnableJpaAuditing" to the main class of the project

@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users; // many to many relationship between Role And User


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate; // to know when the User was created
    @LastModifiedDate
    @Column  (insertable = false)
    private LocalDateTime lastModifiedDate; // know when anything was changed
}
