package com.github.aquilesdias.libraryapi.api.resource;

import com.github.aquilesdias.libraryapi.api.dto.BookDTO;
import com.github.aquilesdias.libraryapi.api.exceptions.ApiErrors;
import com.github.aquilesdias.libraryapi.api.exceptions.BusinessException;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiErrors handlerValidationException(MethodArgumentNotValidException ex){
//        BindingResult bindingResult = ex.getBindingResult();
//        return new ApiErrors(bindingResult);
//    }

    @GetMapping("{id}")
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

    @PutMapping("{id}")
    public BookDTO update(@PathVariable Long id, BookDTO bookDTO){
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(bookDTO.getAuthor());
        book.setTitle(bookDTO.getTitle());
        book = service.update(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        Book book = service.getById(id).orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

//    @ExceptionHandler(BusinessException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiErrors handlerBusinessException( BusinessException ex){
//        return new ApiErrors(ex);
//    }
}
