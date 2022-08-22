package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class LoanController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private LoanRepository loanRepository;

    @GetMapping("/api/loans")
    public List<LoanDTO> getAll(){
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }

    @Transactional
    @PostMapping("/api/loans")

    public ResponseEntity<Object> newLoan(@RequestBody LoanApplicationDTO loanApplicationDTO,
                                          Authentication authentication){

        Client clientConnected = clientRepository.findByEmail(authentication.getName());

        //trae el objeto (cuenta) que corresponde al numero de cuenta que coloca el usuario y lo trae
        //desde el LoanApplicationDTO, donde recibimos el requestbody.
        Account toAccount = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());
        Loan typeLoan = loanRepository.findById(loanApplicationDTO.getLoanId()).orElse(null);

        if (clientConnected != null){

            if (loanApplicationDTO.getLoanId() == 0 || loanApplicationDTO.getAmount() <= 0 ||
                    loanApplicationDTO.getPayments() <= 0){
                return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
            }

            if (typeLoan == null){
                return new ResponseEntity<>("Loan does not exist", HttpStatus.FORBIDDEN);
            }

            if (loanApplicationDTO.getAmount() > typeLoan.getMaxAmount()){
                return new ResponseEntity<>("The amount cannot exceed the declared limit.", HttpStatus.FORBIDDEN);
            }

            if (!typeLoan.getPayments().contains(loanApplicationDTO.getPayments())){
                return new ResponseEntity<>("The payment is invalid", HttpStatus.FORBIDDEN);
            }

            if (toAccount == null){
                return new ResponseEntity<>("The account does not exist", HttpStatus.FORBIDDEN);
            }

            //valida que la cuenta destino
            if (!accountRepository.findByClient(clientConnected).contains(toAccount)){
                return new ResponseEntity<>("The account does not belong to the authenticated client.", HttpStatus.FORBIDDEN);
            }

            ClientLoan createClientLoan = new ClientLoan((loanApplicationDTO.getAmount())*1.2, loanApplicationDTO.getPayments(), clientConnected, typeLoan );
            Transaction loanTransaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(),
                    typeLoan.getName() + "- loan approved",toAccount);

            clientLoanRepository.save(createClientLoan);
            transactionRepository.save(loanTransaction);

            Double newBalance = toAccount.getBalance() + loanApplicationDTO.getAmount();
            toAccount.setBalance(newBalance);

            accountRepository.save(toAccount);

            return new ResponseEntity<>("Loan created successfully.", HttpStatus.CREATED);

        }else {
            return new ResponseEntity<>("Cliente no autorizado", HttpStatus.FORBIDDEN);
        }
    }
}