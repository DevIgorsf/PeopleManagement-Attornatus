package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record PessoaCadastro(
        @NotBlank(message = "Nome é obrigatório")
        String nome,
        @NotNull(message = "Data de nascimento é obrigatório")
        @Past(message = "Data deve ser anterior a data atual")
        LocalDate nascimento
){
}
