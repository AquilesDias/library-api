package com.github.aquilesdias.libraryapi.api.resource;

import com.github.aquilesdias.libraryapi.api.dto.BookDTO;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/books")
public class BookController {

    @Autowired
    private BookService service;

//    private BookService service;
//
//    public BookController(BookService service){
//        this.service = service;
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO bookDTO(@RequestBody BookDTO dto){

        Book entity = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .build();

        service.save(entity);

        return BookDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .isbn(entity.getIsbn())
                .build();

    }
}
