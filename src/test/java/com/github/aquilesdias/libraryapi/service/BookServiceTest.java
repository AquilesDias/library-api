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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

        Mockito.when(repository.existsByIdIsbn(Mockito.anyString())).thenReturn(false);

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

    private static Book createValidBook() {
        return Book.builder().isbn("123").title("O rei leao").author("Disney").build();
    }


    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shoulNotSaveABookWithDuplicatedISBN(){

        //cenario
        Book book = createValidBook();
        Mockito.when( repository.existsByIdIsbn(Mockito.anyString())).thenReturn(true);

        //execução
        Throwable exception = Assertions.catchThrowable( () -> bookService.save(book));

        //verificação
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN já existe!");

        Mockito.verify(repository, Mockito.never()).save(book);

    }
}
