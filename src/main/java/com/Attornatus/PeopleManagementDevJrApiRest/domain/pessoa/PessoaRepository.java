package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {
    Optional<Pessoa> findByNome(String nome);
}
