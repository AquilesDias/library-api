package com.github.aquilesdias.libraryapi.model.repository;

import com.github.aquilesdias.libraryapi.model.BookRepository;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar true quando o isbn existir.")
    public void returnTrueWhenIsbnExists(){

        //cenario
        String isbn = "123";

        Book book = createNewBook(isbn);

        testEntityManager.persist(book);

        //execucao
        boolean exists = bookRepository.existsByIsbn(isbn);

        //verificação
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando isbn não existir")
    public void returnFalseWhenIsbnDoesntExists(){

        String isbn = "123";

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isFalse();
    }


    @Test
    @DisplayName("Deve obter um livro por id")
    public void findById(){

        //Cenario
        Long id = 1l;
        Book book = createNewBook("123");
        testEntityManager.persist(book);

        //Execucao
        Optional<Book> foundBook = bookRepository.findById(id);

        //Verificao
        assertThat( foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um book")
    public void saveBookTest(){

        Book book = createNewBook("123");

        Book bookSaved = bookRepository.save(book);

        assertThat( bookSaved.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){

        Book book = createNewBook(" 123");
        testEntityManager.persist(book);

        Book foundBook = testEntityManager.find(Book.class, book.getId());

        bookRepository.delete(foundBook);

        Book bookDeleted = testEntityManager.find(Book.class, book.getId());
        assertThat( bookDeleted ).isNull();
    }

    public static Book createNewBook(String isbn) {
        return Book.builder()
                .title("The Last of us")
                .author("Greg")
                .isbn(isbn)
                .build();
    }
}
