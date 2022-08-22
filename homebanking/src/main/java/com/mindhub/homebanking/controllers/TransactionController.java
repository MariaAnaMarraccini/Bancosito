package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
public class TransactionController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    @PostMapping("/api/transactions")
    private ResponseEntity<Object> createTransaction(
            @RequestParam Double amount, @RequestParam String description,
            @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
            Authentication authentication) {

        Client clientConnected = clientRepository.findByEmail(authentication.getName());
        //Busca si la cuenta de origen existe en el repositorio de accounts
        Account fromAccount = accountRepository.findByNumber(fromAccountNumber);
        //Busca si la cuenta de destino existe en el repositorio de accounts
        Account toAccount = accountRepository.findByNumber(toAccountNumber);

        if (clientConnected != null){

            if (amount <= 0 || description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty() ){
                return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
            }

            if (fromAccountNumber.equals(toAccountNumber)){
                return new ResponseEntity<>("Error. It's the same account", HttpStatus.FORBIDDEN);
            }

            if (fromAccount == null){
                return new ResponseEntity<>(" fromAccount does not exist", HttpStatus.FORBIDDEN);
            }

            if (!accountRepository.findByClient(clientConnected).contains(fromAccount)){
                return new ResponseEntity<>("The account isn't from the connected client", HttpStatus.FORBIDDEN);
            }

            if (toAccount == null){
                return new ResponseEntity<>("toAccount does not exist", HttpStatus.FORBIDDEN);
            }

            if (fromAccount.getBalance() < amount){
                return new ResponseEntity<>("Insufficient balance", HttpStatus.FORBIDDEN);
            }


            //No solicita la fecha porque en el constructor coloque por defecto LocalDate.now()
            transactionRepository.save(new Transaction(TransactionType.DEBIT, -(amount), description + " " + toAccountNumber,
                    fromAccount));

            transactionRepository.save(new Transaction(TransactionType.CREDIT, amount, description + " " + fromAccountNumber,
                    toAccount));

            Double fromAccountNewBalance = fromAccount.getBalance() - amount;
            Double toAccountNewBalance = toAccount.getBalance() + amount;

            fromAccount.setBalance(fromAccountNewBalance);
            toAccount.setBalance(toAccountNewBalance);

            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            return new ResponseEntity<>("Succesful transaction", HttpStatus.CREATED);


        } else{
            return new ResponseEntity<>("Cliente no autorizado", HttpStatus.FORBIDDEN);
        }
    }
}
