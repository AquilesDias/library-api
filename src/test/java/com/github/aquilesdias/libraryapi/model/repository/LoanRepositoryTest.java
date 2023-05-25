package com.github.aquilesdias.libraryapi.model.repository;

import com.github.aquilesdias.libraryapi.model.LoanRepository;
import com.github.aquilesdias.libraryapi.model.entity.Book;
import com.github.aquilesdias.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.github.aquilesdias.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository loanRepository;

    @Test
    @DisplayName("Deve verificar se existe emprestimo não devolvido para livro.")
    public void existsByBookAndNotReturnedTest(){

        Book book = createNewBook("123");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Hercules").localDate(LocalDate.now()).build();
        entityManager.persist(loan);

        boolean exists = loanRepository.existsByBookAndNotReturned(book);

        assertThat( exists ).isTrue();

    }

    @Test
    @DisplayName("Deve buscar emprestimo por ISBN ou CUSTOMER")
    public void findByBookIsbnOrCustomerTest(){

        createAndPersist(LocalDate.now());

        Page<Loan> result = loanRepository.findByBookIsbnOrCustomer(
                "123",
                "Hercules",
                PageRequest.of(1, 10)
        );

//        assertThat( result.getContent() ).hasSize(1);
        assertThat( result.getPageable().getPageNumber()).isEqualTo(1);
        assertThat( result.getPageable().getPageSize()).isEqualTo(10);
        assertThat( result.getTotalElements()).isEqualTo(1);

    }

    @Test
    @DisplayName("Deve obter emprestimos onde data emprestimo for menor ou igual a tres dias atras e não retornados")
    public void findByLoanDateLessThanAndNotReturned(){

        Loan loan = createAndPersist( LocalDate.now().minusDays(5));

        List<Loan> result = loanRepository
                .findByLoanDateLessThanAndNotReturned(
                        LocalDate
                                .now()
                                .minusDays(4));

        assertThat( result ).hasSize(1).contains(loan);

    }

    @Test
    @DisplayName("Deve retornar vazio quando não houver emprestimos atrasados.")
    public void notFindByLoanDateLessThanAndNotReturned(){

        Loan loan = createAndPersist( LocalDate.now());

        List<Loan> result = loanRepository
                .findByLoanDateLessThanAndNotReturned(
                        LocalDate
                                .now()
                                .minusDays(4));

        assertThat( result ).isEmpty();

    }

    private Loan createAndPersist(LocalDate loanDate){

        Book book = createNewBook("123");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Hercules").localDate(loanDate).build();
        entityManager.persist(loan);

        return loan;
    }
}
