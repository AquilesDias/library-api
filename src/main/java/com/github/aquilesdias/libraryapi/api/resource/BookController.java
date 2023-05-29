package com.github.aquilesdias.libraryapi.api.resource;

import com.github.aquilesdias.libraryapi.api.dto.BookDTO;
import com.github.aquilesdias.libraryapi.api.dto.LoanDTO;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.model.entity.Loan;
import com.github.aquilesdias.libraryapi.service.BookService;
import com.github.aquilesdias.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
@Api("API BOOK")
@RestController
@RequestMapping("api/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService service;

    private final LoanService loanService;

    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Create a book.")
    public BookDTO bookDTO(@RequestBody @Valid BookDTO dto) {

        Book entity = modelMapper.map(dto, Book.class);

        service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    @ApiOperation("Find book by ID.")
    public BookDTO findById(@PathVariable("id") Long id){

        return service.getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageable){
        Book filter = modelMapper.map(bookDTO, Book.class);
        Page<Book> result = service.find(filter, pageable);
        List<BookDTO> list = result.getContent()
                        .stream()
                        .map(entity -> modelMapper.map(entity, BookDTO.class))
                        .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageable, result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> loanByBook(@PathVariable Long id, Pageable pageable){

        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoanByBook(book, pageable);
        List<LoanDTO> list = result.getContent()
                .stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
    }

    @PutMapping("{id}")
    @ApiOperation("Update a book")
    public BookDTO update(@PathVariable Long id, BookDTO bookDTO){
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(bookDTO.getAuthor());
        book.setTitle(bookDTO.getTitle());
        book = service.update(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Delete book by id.")
    @ApiResponses({ @ApiResponse(code = 204, message = " (No Content) Book Sucessefully deleted.")})
    public void delete(@PathVariable Long id){
        Book book = service.getById(id).orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

}
