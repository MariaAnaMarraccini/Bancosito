package com.mindhub.homebanking.controllers;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;

    @GetMapping("/api/clients")
    public List<ClientDTO> getAll() {

        return clientRepository.findAll().stream().map(client -> new ClientDTO(client)).collect(Collectors.toList());
    }

    /*@RequestMapping("/clients/{id}")
    public Optional<Client> getClient (@PathVariable Long id){
        return clientRepository.findById(id);
    }*/

    @GetMapping("api/clients/{id}")
    public ClientDTO getClientById(@PathVariable Long id) {
        return new ClientDTO(clientRepository.findById(id).get());
    }

    @GetMapping("/api/clients/current")
    public ClientDTO getClient(Authentication authentication) {
        Client client = this.clientRepository.findByEmail(authentication.getName());
        return new ClientDTO(client);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/api/clients")
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (clientRepository.findByEmail(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
        //Creo la cuenta para el cliente nuevo, para asignarlo al cliente en cuestion, lo hago por findByEmail(email) --> hace referencia al cliente creado.
        accountRepository.save(new Account("VIN-" + AccountUtils.createAccountNumber(10000000, 100000001), LocalDateTime.now(), 0.00, clientRepository.findByEmail(email)));
        return new ResponseEntity<>("Account created", HttpStatus.CREATED);

    }
}

