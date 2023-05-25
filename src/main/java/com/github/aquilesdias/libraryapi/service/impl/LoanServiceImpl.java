package com.github.aquilesdias.libraryapi.service.impl;

import com.github.aquilesdias.libraryapi.api.dto.LoanFilterDTO;
import com.github.aquilesdias.libraryapi.api.exceptions.BusinessException;
import com.github.aquilesdias.libraryapi.model.LoanRepository;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.model.entity.Loan;
import com.github.aquilesdias.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository){
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if( loanRepository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return loanRepository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return loanRepository.save(loan);
    }

    public List<Loan> getAllLateLoan(){
        final Integer loanDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
        return loanRepository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable page) {
        return loanRepository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), page);
    }

    @Override
    public Page<Loan> getLoanByBook(Book book, Pageable pageable) {
        return loanRepository.findByBook(book, pageable);
    }
}
