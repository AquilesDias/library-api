package com.github.aquilesdias.libraryapi.api.resource;

import com.github.aquilesdias.libraryapi.api.dto.BookDTO;
import com.github.aquilesdias.libraryapi.api.exceptions.ApiErrors;
import com.github.aquilesdias.libraryapi.api.exceptions.BusinessException;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("api/books")
public class BookController {

    @Autowired
    private BookService service;

    @Autowired
    private ModelMapper modelMapper;

//    private BookService service;
//
//    public BookController(BookService service){
//        this.service = service;
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO bookDTO(@RequestBody @Valid BookDTO dto){

        Book entity = modelMapper.map(dto, Book.class);

//        Book entity = Book.builder()
//                .title(dto.getTitle())
//                .author(dto.getAuthor())
//                .isbn(dto.getIsbn())
//                .build();

        service.save(entity);

        return modelMapper.map(entity, BookDTO.class);

//        return BookDTO.builder()
//                .id(entity.getId())
//                .title(entity.getTitle())
//                .author(entity.getAuthor())
//                .isbn(entity.getIsbn())
//                .build();
//
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handlerValidationException(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @GetMapping("{id}")
    public BookDTO findById(@PathVariable("id") Long id){

        return service.getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        Book book = service.getById(id).orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(id);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handlerBusinessException( BusinessException ex){
        return new ApiErrors(ex);
    }
}
