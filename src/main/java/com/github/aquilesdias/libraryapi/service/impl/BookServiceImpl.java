package com.github.aquilesdias.libraryapi.service.impl;

import com.github.aquilesdias.libraryapi.api.exceptions.BusinessException;
import com.github.aquilesdias.libraryapi.model.BookRepository;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Optional<Book> getById(Long id){
        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("Não foi possivel deletar");
        }
        this.repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("Não foi possivel atualizar");
        }
        return repository.save(book);
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageable) {

        Example<Book> example = Example.of(
                filter,
                ExampleMatcher
                    .matching()
                    .withIgnoreCase()
                    .withIgnoreNullValues()
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example, pageable);
    }

    @Override
    public Optional<Book> getBookByIsbn(String number) {
        return Optional.empty();
    }
}
