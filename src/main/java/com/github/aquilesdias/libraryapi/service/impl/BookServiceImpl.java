package com.github.aquilesdias.libraryapi.service.impl;

import com.github.aquilesdias.libraryapi.api.exceptions.BusinessException;
import com.github.aquilesdias.libraryapi.model.BookRepository;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {


    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {

        if( repository.existsByIsbn( book.getIsbn()) ){
          throw new BusinessException("ISBN já existe!");
        }

        return repository.save(book);
    }

    public Optional<Book> getById(){
        return Optional.empty();
    }
}
