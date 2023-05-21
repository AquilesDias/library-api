package com.github.aquilesdias.libraryapi.api.resource;

import com.github.aquilesdias.libraryapi.api.dto.BookDTO;
import com.github.aquilesdias.libraryapi.api.dto.LoanDTO;
import com.github.aquilesdias.libraryapi.api.dto.LoanFilterDTO;
import com.github.aquilesdias.libraryapi.api.dto.ReturnedLoanDTO;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.model.entity.Loan;
import com.github.aquilesdias.libraryapi.service.BookService;
import com.github.aquilesdias.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final BookService bookService;
    private final LoanService loanService;

    private ModelMapper modelMapper;


    @GetMapping
    public Page<LoanDTO> find (LoanFilterDTO loanFilterDTO, Pageable pageable){

        Page<Loan> result = loanService.find( loanFilterDTO, pageable);

        List<LoanDTO> list = result.getContent()
                .stream()
                .map(entity -> {
                    Book book = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map( entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                   return loanDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
    }

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

        Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned( dto.getReturnedBook() );
        loanService.update(loan);
    }
}
