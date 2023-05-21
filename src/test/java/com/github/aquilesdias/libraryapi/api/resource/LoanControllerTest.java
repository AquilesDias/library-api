package com.github.aquilesdias.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aquilesdias.libraryapi.api.dto.LoanDTO;
import com.github.aquilesdias.libraryapi.api.dto.LoanFilterDTO;
import com.github.aquilesdias.libraryapi.api.dto.ReturnedLoanDTO;
import com.github.aquilesdias.libraryapi.api.exceptions.BusinessException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
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

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro emprestado")
    public void loanedBookErrorOnCreateLoanTest() throws Exception{

        LoanDTO loanDTO = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String  json    = new ObjectMapper().writeValueAsString(loanDTO);

        Book book = Book.builder().id(1L).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect( status().isBadRequest())
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( jsonPath("errors[0]").value("Book already loaned"));

    }

    @Test
    @DisplayName("Deve retornar um livro")
    public void returnedBookTest() throws Exception{

        ReturnedLoanDTO returnedLoanDTO = ReturnedLoanDTO.builder().returnedBook(true).build();
        Loan loan = Loan.builder().id(1l).build();

        BDDMockito.given( loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(returnedLoanDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch( LOAN_API.concat("/1") )
                .content( json )
                .contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON );

        mvc.perform(request)
                .andExpect( status().isNotFound() );

        Mockito.verify( loanService, Mockito.times(0)).update(loan);
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar devolver livro inexistente.")
    public void returnedInexistentBookTest() throws Exception {

        ReturnedLoanDTO returnedLoanDTO = ReturnedLoanDTO.builder().returnedBook(true).build();

        String json = new ObjectMapper().writeValueAsString(returnedLoanDTO);

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(LOAN_API.concat("/1"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);


        mvc.perform(request)
                .andExpect( status().isNotFound() );
    }

    @Test
    @DisplayName("Deve filtrar emprestimo por parametro.")
    public void filterLoanTest() throws Exception{

        Book book = Book.builder().id(1L).isbn("123").build();

        Loan loan = Loan.builder().id(1L).isbn("123").customer("Douglas").build();
        loan.setBook(book);


        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)) )
                .willReturn( new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1)) ;

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=10", book.getIsbn(),loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }
}
