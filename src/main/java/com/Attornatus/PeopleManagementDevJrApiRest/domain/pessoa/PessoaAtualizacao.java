package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record PessoaAtualizacao(
        @NotNull
        UUID id,
        String nome,
        LocalDate nascimento) {
}
