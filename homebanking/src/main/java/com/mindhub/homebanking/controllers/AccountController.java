package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/api/accounts")
    public List<AccountDTO> getAll(){
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(toList());
    }

    /*@RequestMapping("/accounts/{id}")
    public Optional<Account> getAccount (@PathVariable Long id){
        return accountRepository.findById(id);
    }*/

    @GetMapping("/api/accounts/{id}")
    public AccountDTO getAccountById(@PathVariable Long id){
        return new AccountDTO(accountRepository.findById(id).get());
    }

    //Traemos la lista de cuentas
    @GetMapping("/api/clients/current/accounts")
    public List<AccountDTO> getAccounts(Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());
        return client.getAccounts().stream().map(AccountDTO::new).collect(toList());
    }

    @PostMapping("/api/clients/current/accounts")
    //OBJECT es la clase principal dentro de java y engloba todos los tipos de datos ,por tanto puede devolver cualquier tipo de dato.
    private ResponseEntity<Object> registerAccount(Authentication authentication) { //Devuelve respuesta de acuerdo al usuario autenticado
        Client clientConnected = clientRepository.findByEmail(authentication.getName()); //Obtenemos el cliente logueado y autenticado.

        if (clientConnected != null) {
            if (clientConnected.getAccounts().size() > 2) { //retorna el numero de elementos en el set de account
                return new ResponseEntity<>("El limite de cuentas por cliente es de 3", HttpStatus.FORBIDDEN);
            } else {
                //Si se cumple la condicion, se crea la cuenta
                Account account = new Account("VIN-" + AccountUtils.createAccountNumber(10000000, 100000001), LocalDateTime.now(), 0.00, clientConnected);
                //Guardar la cuenta en el repositorio de cuentas
                accountRepository.save(account);

                return new ResponseEntity<>(account, HttpStatus.CREATED);
            }
        } else {
            return new ResponseEntity<>("Cliente no autorizado", HttpStatus.FORBIDDEN);
        }
    }

}
