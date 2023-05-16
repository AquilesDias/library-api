package com.github.aquilesdias.libraryapi.api.resource;

import com.github.aquilesdias.libraryapi.api.dto.LoanDTO;
import com.github.aquilesdias.libraryapi.api.dto.ReturnedLoanDTO;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.model.entity.Loan;
import com.github.aquilesdias.libraryapi.service.BookService;
import com.github.aquilesdias.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final BookService bookService;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO){
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan entity = Loan.builder()
                .book(book)
                .customer(loanDTO.getCustomer())
                .isbn(loanDTO.getIsbn())
                .localDate(LocalDate.now())
                .build();

        entity = loanService.save(entity);
        return entity.getId();
    }

    @PatchMapping(" {id} ")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){

        Loan loan = loanService.getById(id).get();
        loan.setReturned( dto.getReturnedBook() );
        loanService.update(loan);
    }
}
