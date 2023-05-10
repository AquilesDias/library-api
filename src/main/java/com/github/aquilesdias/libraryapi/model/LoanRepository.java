package com.github.aquilesdias.libraryapi.model;

import com.github.aquilesdias.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
