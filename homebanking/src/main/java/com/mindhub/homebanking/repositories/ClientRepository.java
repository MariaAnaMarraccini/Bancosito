package com.mindhub.homebanking.repositories;

//Entidades: tablas de una base de datos (se volvera clase)

import com.mindhub.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource

//Este repositorio nos va a permitir crear y guardar datos (tabla de base de datos)
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByEmail(String email);
}
