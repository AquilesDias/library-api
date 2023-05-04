package com.github.aquilesdias.libraryapi.service;


import com.github.aquilesdias.libraryapi.api.exceptions.BusinessException;
import com.github.aquilesdias.libraryapi.model.BookRepository;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;
    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.bookService = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){

        //cenario
        Book book = createValidBook();

        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when( repository.save(book))
                .thenReturn(
                        Book.builder()
                                .id(1l)
                                .isbn("123")
                                .author("Disney")
                                .title("O rei leao")
                                .build()
                );

        //execucao
        Book savedBook = bookService.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("O rei leao");
        assertThat(savedBook.getAuthor()).isEqualTo("Disney");
    }

    @Test
    @DisplayName("Deve encontrar um book por id")
    public void findByIdTest(){
        //Cenario
        Long id = 1l;

        Book book = createValidBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //Execucao
        Optional<Book> foundBook = bookService.getById(id);

        //Verificação
        assertThat( foundBook.isPresent()).isTrue();
        assertThat( foundBook.get().getId()).isEqualTo(book.getId());
        assertThat( foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat( foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat( foundBook.get().getIsbn()).isEqualTo(book.getIsbn());

    }
    
    
    @Test
    @DisplayName("Deve retornar um book por propriedades.")
    public void findBookTest(){

        //Cenario
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);

        Mockito.when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        //Execução
        Page<Book> result = bookService.find(book, pageRequest);

        //Verificação
        assertThat( result.getTotalElements()).isEqualTo(1);
        assertThat( result.getContent()).isEqualTo(list);
        assertThat( result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat( result.getPageable().getPageSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("Deve retornar vazio quando o livro não for encontado por id")
    public void bookNotFoundFindByIdTest(){

        //Cenario
        Long id = 1l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //Execucao
        Optional<Book> notFoundBook = bookService.getById(id);

        //Verificacao
        assertThat( notFoundBook.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro por id")
    public void deleteBookTest(){
        // Cenario
        Book book = Book.builder().id(1l).build();

        // Execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> bookService.delete(book));

        // Verificação
        Mockito.verify( repository, Mockito.timeout(1)).delete(book);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar deletar livro inexistente.")
    public void deleteInvalidBookTest(){

        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                bookService.delete(book));

        Mockito.verify(repository, Mockito.never() ).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest(){

        // Cenario
        long id = 1l;

        // Livro a ser atualizado
        Book updatingBook = Book.builder().id(id).build();

        // Simulação
        Book updateBook = createValidBook();
        updateBook.setId(id);
        Mockito.when(repository.save(updatingBook)).thenReturn(updateBook);

        // Execução
        Book book = bookService.update(updatingBook);

        // Verificação
        assertThat( book.getId() )     .isEqualTo(updateBook.getId());
        assertThat( book.getTitle() )  .isEqualTo( updateBook.getTitle() )  ;
        assertThat( book.getAuthor() ) .isEqualTo( updateBook.getAuthor() ) ;
        assertThat( book.getIsbn() )   .isEqualTo( updateBook.getIsbn() )   ;

    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar um livro.")
    public void updateInvalidBookTest(){

        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookService.update(book));

        Mockito.verify( repository, Mockito.never()).save(book);
    }

    private static Book createValidBook() {
        return Book.builder().isbn("123").title("O rei leao").author("Disney").build();
    }


    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shoulNotSaveABookWithDuplicatedISBNTest(){

        //cenario
        Book book = createValidBook();
        Mockito.when( repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execução
        Throwable exception = Assertions.catchThrowable( () -> bookService.save(book));

        //verificação
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN já existe!");

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve obter um book por ISBN")
    public void getBookByIsbnTest(){

        String isbn = "123";

        Mockito.when( repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));

        Optional<Book> book = bookService.getBookByIsbn(isbn);

        assertThat( book.isPresent()     ).isTrue();
        assertThat( book.get().getId()   ).isEqualTo(1L);
        assertThat( book.get().getIsbn() ).isEqualTo("123");

        Mockito.verify(repository, Mockito.times(1)).findByIsbn(isbn);

    }
}
