package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
//@DataJpaTest //Se asegura que se enlacen los repositorios y que trabaje con H2
@AutoConfigureTestDatabase() //Permite revertir los datos una vez se termina de testear

public class RepositoriesTest {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Test //Se asegura que los prestamos no esten vacios cuando los pidan
    public void existLoans(){

        List<Loan> loans = loanRepository.findAll();

        //Metodos/funciones de los testeos, se asegura que los prestamos no esten vacios.
        assertThat(loans,is(not(empty()))); //--> metodo estatico
    }

    @Test
    public void existPersonalLoan(){
        List<Loan> loans = loanRepository.findAll();
        //Busca en la lista por lo menos uno que tenga de nombre "Peronal"
        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));
    }

    //Client
    @Test
    public void existClient(){
        List<Client> clients = clientRepository.findAll();
        assertThat(clients,is(not(empty())));
    }
    @Test
    public void correctPasswordClient(){
        List<Client> clients = clientRepository.findAll();
        assertThat(clients,  hasItem(hasProperty("password", startsWith("{bcrypt}$"))));
    }

    //Card
    @Test
    public void existCard(){
        List<Card> cards = cardRepository.findAll();
        assertThat(cards,is(not(empty())));
    }
    @Test
    public void correctNumberCard(){
        List<Card> cards = cardRepository.findAll();
        assertThat(cards,is(hasItem(hasProperty("number", startsWith("4657")))));
    }

    @Test
    public void existCreditCards(){
        List<Card> cards = cardRepository.findByType(CardType.CREDIT);
        assertThat(cards,is(not(empty())));
    }

    //Account
    @Test
    public void existAccount(){
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts,is(not(empty())));
    }
    @Test
    //Trae todas las cuentas
    public void correctNumberAccount(){
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts,is(hasItem(hasProperty("number", startsWith("VIN")))));
    }

    //Transaction
    @Test
    public void existTransaction(){
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions,is(not(empty())));
    }
    @Test
    public void existCreditTransaction(){
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions, hasItem(hasProperty("type", is(TransactionType.CREDIT))));
    }

}

