package com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record EnderecoForm(
        @NotBlank
        String logradouro,
        @NotBlank
        @Pattern(regexp = "\\d{8}")
        String cep,
        @NotBlank
        @Positive
        String numero,
        @NotBlank
        String cidade ) {
}
