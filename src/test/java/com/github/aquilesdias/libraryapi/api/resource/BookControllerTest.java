package com.github.aquilesdias.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aquilesdias.libraryapi.api.dto.BookDTO;
import com.github.aquilesdias.libraryapi.api.exceptions.BusinessException;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.OffsetTime;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTeste() throws Exception{

        BookDTO dto = createNewBook();

        Book saveBook = Book.builder().id(1L).title("Senhor dos Aneis").author("J. R. R. Tolkien").isbn("8533613377").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(dto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));

    }



    @Test
    @DisplayName("Deve lançar um erro de validação quando houver ausencia de dados para criar livro. ")
    public void createInvalidBookTeste() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));


    }

    @Test
    @DisplayName("Deve lançar um erro ao tentar cadastrar um livro com o isbn já existente.")
    public void createBookWithDuplicatedIsbn() throws Exception {

        String mensagemErro = "ISBN já existe!";

        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(mensagemErro));

    }

    @Test
    @DisplayName("Deve obter informações de um livro")
    public void getBookDetailsTest() throws Exception{

        Long id = 1l;

        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" +id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar um livro.")
    public void bookNotFoundTest() throws Exception{

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty() );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void delete() throws Exception{

        Book book = Book.builder().id(1l).build();
        BDDMockito.given(service.getById(Mockito.anyLong()) ).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" +1));

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {

        Long id = 1l;

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book updatingBook = Book.builder().id(id).title("Avangers").author("Marvel").isbn("321").build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));

        Book updateBook = Book.builder().id(1l).title("Senhor dos Aneis").author("J. R. R. Tolkien").isbn("8533613379").build();
        BDDMockito.given(service.update(updatingBook)).willReturn(updateBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" +1))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("8533613379"));
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar livro para atualizar")
    public void updateNotFoundTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" +1))
                .accept(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar livro para deletar.")
    public void deleteInexistentBookTest() throws Exception{

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" +1));

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private static BookDTO createNewBook() {
        return BookDTO.builder().title("Senhor dos Aneis").author("J. R. R. Tolkien").isbn("8533613377").build();
    }
}
