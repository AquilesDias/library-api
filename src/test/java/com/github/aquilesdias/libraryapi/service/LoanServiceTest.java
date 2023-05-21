package com.github.aquilesdias.libraryapi.service;

import com.github.aquilesdias.libraryapi.api.dto.LoanFilterDTO;
import com.github.aquilesdias.libraryapi.api.exceptions.BusinessException;
import com.github.aquilesdias.libraryapi.model.LoanRepository;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.model.entity.Loan;
import com.github.aquilesdias.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest(){

        Book book = Book.builder().id(1l).build();

        Loan savingLoan = Loan.builder().customer("Harry").localDate(LocalDate.now()).book(book).build();

        Loan savedLoan = Loan.builder().id(1l).customer("Harry").localDate(LocalDate.now()).book(book).build();

        Mockito.when( repository.existsByBookAndNotReturned(book)).thenReturn(false);
        Mockito.when( repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat( loan.getId()).isEqualTo( savedLoan.getId());
        assertThat( loan.getLocalDate()).isEqualTo( savedLoan.getLocalDate());
        assertThat( loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat( loan.getBook().getId()).isEqualTo(savedLoan.getId());

    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar emprestar livro ja emprestado.")
    public void loanedBookTest(){
        Book book = Book.builder().id(1l).build();

        Loan savingLoan = Loan.builder().customer("Harry").localDate(LocalDate.now()).book(book).build();

        Mockito.when( repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable throwable = catchThrowable(() -> service.save(savingLoan));

        assertThat(throwable).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");

        Mockito.verify( repository, Mockito.never() ).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter informações de um emprestimo por ID.")
    public void getDetailsLoanTest(){

        Book book = Book.builder().id(1L).build();
        Loan loan = Loan.builder().customer("Harry").localDate(LocalDate.now()).book(book).build();

        Mockito.when( repository.findById(1L) ).thenReturn( Optional.of(loan) );

        Optional<Loan> result = service.getById(1L);

        assertThat( result.isPresent()         ).isTrue();
        assertThat( result.get().getId()       ).isEqualTo( loan.getId()        );
        assertThat( result.get().getCustomer() ).isEqualTo( loan.getCustomer()  );
        assertThat( result.get().getBook()     ).isEqualTo( loan.getBook()      );
        assertThat( result.get().getLocalDate()).isEqualTo( loan.getLocalDate() );

    }

    @Test
    @DisplayName("Deve atualizar um emprestimo.")
    public void updateLoanTest(){

        Book book = Book.builder().id(1L).build();
        Loan loan = Loan.builder().id(1L).customer("Harry").localDate(LocalDate.now()).book(book).build();
        loan.setReturned( true );

        Mockito.when( repository.save(loan) ).thenReturn(loan);

        Loan updateLoan = service.update(loan);

        assertThat( updateLoan.getReturned() ).isTrue();

        Mockito.verify( repository ).save(loan);
    }

    @Test
    @DisplayName("Deve retornar um book por propriedades.")
    public void findBookTest(){

        //Cenario
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().isbn("123").customer("Harry").build();

        Loan loan = Loan.builder().id(1l).customer("Harry").localDate(LocalDate.now()).build();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> list = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(list, pageRequest, list.size());
        Mockito.when( repository.findByBookIsbnOrCustomer(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PageRequest.class)))
        .thenReturn(page);

        //Execução
        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

        //Verificação
        assertThat( result.getTotalElements()).isEqualTo(1);
        assertThat( result.getContent()).isEqualTo(list);
        assertThat( result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat( result.getPageable().getPageSize()).isEqualTo(10);

    }
}
