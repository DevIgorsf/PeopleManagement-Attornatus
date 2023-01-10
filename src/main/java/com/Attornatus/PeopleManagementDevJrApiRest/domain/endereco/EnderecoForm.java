package com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record EnderecoForm(
        @NotBlank(message = "Logradouro é obrigatório")
        String logradouro,
        @NotBlank(message = "Cep é obrigatório")
        @Pattern(regexp = "\\d{8}", message = "Formato é de 8 números")
        String cep,
        @NotBlank(message = "Número é obrigatório")
        @Positive(message = "Número deve ser positivo")
        String numero,
        @NotBlank(message = "Cidade é obrigatório")
        String cidade ) {
}
