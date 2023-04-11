package com.Attornatus.PeopleManagementDevJrApiRest.builder;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaCadastro;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class PessoaCadastroBuilder {

    @Builder.Default
    private String nome = "Giovanna Dafne";
    @Builder.Default
    private LocalDate nascimento = LocalDate.parse("1997-01-23");

    public PessoaCadastro toPessoaCadastro() {
        return new PessoaCadastro(
                nome,
                nascimento);
    }
}
