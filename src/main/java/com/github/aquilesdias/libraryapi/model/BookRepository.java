package com.github.aquilesdias.libraryapi.model;

import com.github.aquilesdias.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {


}
