package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication //indica que es un projecto que lo maneja Spring
public class HomebankingApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);

	}

	@Bean
	//Instanciamos el repositorio
	public CommandLineRunner initData(ClientRepository clientRepository,
									  AccountRepository accountRepository,
									  TransactionRepository transactionRepository,
									  LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository,
									  CardRepository cardRepository)  {
		return (args) -> {
			//guardar clientes

			Client client1 = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("1234"));
			clientRepository.save(client1);

			Client client2 = new Client("Camila", "Rojas", "camila@gmail.com", passwordEncoder.encode("1234"));
			clientRepository.save(client2);

			System.out.println(client1);

			//Creo la variable "today" para realizar cuentas con el LocalDateTime
			LocalDateTime today = LocalDateTime.now();

			Account account1 = new Account("VIN001", LocalDateTime.now(), 5000.00,client1);
			Account account2 = new Account("VIN002", today.plusDays(+1), 7500.00,client1);
			Account account3 = new Account("VIN003", today.plusDays(+2), 6230.00,client2);
			Account account4 = new Account("VIN004", today.plusDays(+2), 5730.00, client2);

			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);

			Transaction transaction1 = new Transaction(TransactionType.CREDIT,6754.50,"Devolución Pago", account1 );
			Transaction transaction2 = new Transaction(TransactionType.DEBIT,-150.90,"Compra Kiosco", account1 );
			Transaction transaction3 = new Transaction(TransactionType.DEBIT,-200.00,"Carga Sube", account1 );
			Transaction transaction4 = new Transaction(TransactionType.CREDIT,70000.00,"Pago de Haberes ", account2 );
			Transaction transaction5 = new Transaction(TransactionType.DEBIT,-10000.00,"Pago Consorcio", account2 );
			Transaction transaction6 = new Transaction(TransactionType.CREDIT,25650.00,"Devolución a favor", account3 );


			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction6);

			List<Integer> paymentsHipotecario = List.of(12,24,36,48,60);
			List<Integer> paymentsPersonal = List.of(6,12,24);
			List<Integer> paymentsAutomotriz = List.of(6,12,24,36);

			Loan loan1 = new Loan("Hipotecario", 500000.00, paymentsHipotecario);
			Loan loan2 = new Loan("Personal", 100000.00, paymentsPersonal);
			Loan loan3 = new Loan("Automotriz", 300000.00, paymentsAutomotriz);

			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			ClientLoan clientLoan1 = new ClientLoan(400000.00, 60, client1, loan1);
			ClientLoan clientLoan2 = new ClientLoan(50000.00, 12, client1, loan2);
			ClientLoan clientLoan3 = new ClientLoan(100000.00, 24, client2, loan2);
			ClientLoan clientLoan4 = new ClientLoan(200000.00, 36, client2, loan3);

			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);

			LocalDate fromDate = LocalDate.now();
			LocalDate thruDate = fromDate.plusYears(5);

			Card card1 = new Card(client1.getFirstName() + " " + client1.getLastName(),CardType.DEBIT, CardColor.GOLD, "4657-4379-4165-5579", 467, fromDate, thruDate, client1);
			Card card2 = new Card(client1.getFirstName() + " " + client1.getLastName(), CardType.CREDIT, CardColor.TITANIUM, "4467-8921-0047-8756", 134, fromDate, thruDate, client1);
			Card card3 = new Card(client2.getFirstName() + " " + client2.getLastName(), CardType.CREDIT, CardColor.SILVER, "4679-8546-3421-1658", 998,fromDate, thruDate.plusYears(-2), client2);

			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);

		};
	}
}
