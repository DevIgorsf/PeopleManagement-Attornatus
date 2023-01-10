package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.UUID;

public record PessoaAtualizacao(
        @NotNull(message = "Id é obrigatório")
        UUID id,
        String nome,
        @Past(message = "Data deve ser anterior a data atual")
        LocalDate nascimento) {
}
