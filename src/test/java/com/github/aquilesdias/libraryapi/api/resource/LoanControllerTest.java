package com.github.aquilesdias.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aquilesdias.libraryapi.api.dto.LoanDTO;
import com.github.aquilesdias.libraryapi.model.entity.Loan;
import com.github.aquilesdias.libraryapi.service.LoanService;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    static final String LOAN_API = "/api/loan";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo.")
    public void createLoadTest() throws Exception{

        LoanDTO loadDTO = LoanDTO.builder().isbn("123").customer("Douglas").build();
        String json = new ObjectMapper().writeValueAsString(loadDTO);

        Book book = Book.builder().id(1l).isbn("123").build();

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1l).customer("Douglas").localDate(LocalDate.now()).build();

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOAN_API)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
                .andExpect( status().isCreated())
                .andExpect( content().string("1"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de livro inexistente.")
    public void invalidIsbnCreateLoanTest() throws Exception{

        LoanDTO loadDTO = LoanDTO.builder().isbn("123").customer("Douglas").build();
        String json = new ObjectMapper().writeValueAsString(loadDTO);

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.empty() );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));


    }
}
