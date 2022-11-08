package com.github.aquilesdias.libraryapi.api.resource;

import com.github.aquilesdias.libraryapi.api.dto.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/books")
public class BookController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO bookDTO(@RequestBody BookDTO dto){

        return dto;
    }
}
