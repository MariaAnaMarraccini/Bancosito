package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.plaf.synth.ColorType;
import java.time.LocalDate;

@RestController
public class CardController {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("/api/clients/current/cards")
    private ResponseEntity<Object> registerCard (
            @RequestParam CardType type, @RequestParam CardColor color,
            Authentication authentication){

        Client clientConnected = clientRepository.findByEmail(authentication.getName());

        if (clientConnected != null){

            if (cardRepository.findByTypeAndClient(type, clientConnected).size() > 2){
               return new ResponseEntity<>("El limite de tarjeta de " + type + "es 3", HttpStatus.FORBIDDEN);
            }

            String cardHolder = clientConnected.getFirstName() + " " + clientConnected.getLastName();


            cardRepository.save(new Card(cardHolder, type, color, CardUtils.getCardNumber(1001, 10000), CardUtils.getCVVNumber(101, 1000),
                    LocalDate.now(), LocalDate.now().plusYears(5), clientConnected));

            return  new ResponseEntity<>("Tarjeta creada", HttpStatus.CREATED);

        } else{
            return new ResponseEntity<>("Cliente no autorizado", HttpStatus.FORBIDDEN);
        }

    }


}
