package com.github.aquilesdias.libraryapi.api.dto;

import lombok.Data;

@Data
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
}
