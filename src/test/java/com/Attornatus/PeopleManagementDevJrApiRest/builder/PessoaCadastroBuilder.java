package com.Attornatus.PeopleManagementDevJrApiRest.builder;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaCadastro;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaDto;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Builder
public class PessoaCadastroBuilder {

    @Builder.Default
    private String nome = "Giovanna Dafne";
    @Builder.Default
    private LocalDate nascimento = LocalDate.parse("1997-01-23");

    public PessoaCadastro toPessoaForm() {
        return new PessoaCadastro(
                nome,
                nascimento);
    }
}
