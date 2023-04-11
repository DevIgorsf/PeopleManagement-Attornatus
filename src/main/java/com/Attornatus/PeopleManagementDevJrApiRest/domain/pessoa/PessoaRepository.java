package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {
    Optional<Pessoa> findByNome(String nome);
}
