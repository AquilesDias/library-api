package com.github.aquilesdias.libraryapi.service;

import com.github.aquilesdias.libraryapi.api.dto.LoanFilterDTO;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable page);

    Page<Loan> getLoanByBook(Book book, Pageable pageable);
}
