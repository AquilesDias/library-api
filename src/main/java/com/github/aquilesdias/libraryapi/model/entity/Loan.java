package com.github.aquilesdias.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Loan {

    private Long id;
    private String isbn;
    private Book book;
    private String customer;
    private LocalDate localDate;
    private Boolean returned;
}
