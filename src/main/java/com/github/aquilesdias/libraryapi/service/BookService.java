package com.github.aquilesdias.libraryapi.service;

import com.github.aquilesdias.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Long id);

    Book update(Book book);
}
