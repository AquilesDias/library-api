package com.github.aquilesdias.libraryapi.service;

import com.github.aquilesdias.libraryapi.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
