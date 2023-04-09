package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.List;

public record PessoaCadastro(
        @NotBlank(message = "Nome é obrigatório")
        String nome,
        @NotNull(message = "Data de nascimento é obrigatório")
        @Past(message = "Data deve ser anterior a data atual")
        LocalDate nascimento){
}
