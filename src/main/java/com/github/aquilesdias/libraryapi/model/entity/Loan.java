package com.github.aquilesdias.libraryapi.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn;

    @JoinColumn(name = "id_book")
    @ManyToOne
    private Book book;

    private String customer;
    private String customerEmail;
    private LocalDate localDate;
    private Boolean returned;
}
