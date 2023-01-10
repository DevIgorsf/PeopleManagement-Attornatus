package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record PessoaCadastro(
        @NotBlank
        String nome,
        @NotNull
        @Past
        LocalDate nascimento
){
}
