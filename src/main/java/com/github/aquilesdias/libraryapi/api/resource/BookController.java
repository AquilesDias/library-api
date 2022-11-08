package com.github.aquilesdias.libraryapi.api.resource;

import com.github.aquilesdias.libraryapi.api.dto.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/books")
public class BookController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO bookDTO(){

        BookDTO dto = new BookDTO();
        dto.setId(1L);
        dto.setAuthor("J. K. Rowling");
        dto.setTitle("Harry Potter");
        dto.setIsbn("9780747532743");

        return dto;
    }
}
