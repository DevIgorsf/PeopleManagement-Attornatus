package com.Attornatus.PeopleManagementDevJrApiRest.builder;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.Endereco;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.Pessoa;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Builder
public class PessoaBuilder {

    @Builder.Default
    private UUID id = UUID.fromString("0a692751-a7ce-4dd1-ab80-55f9e653de75");
    @Builder.Default
    private String nome = "Giovanna Dafne";
    @Builder.Default
    private LocalDate nascimento = LocalDate.parse("1997-01-23");
    @Builder.Default
    private List<Endereco> enderecoList= Collections.emptyList();
    public Pessoa toPessoa() {
        return new Pessoa(id,
                nome,
                nascimento,
                enderecoList);
    }
}
