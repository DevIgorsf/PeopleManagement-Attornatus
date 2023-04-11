package com.Attornatus.PeopleManagementDevJrApiRest.builder;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.Endereco;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.Pessoa;
import lombok.Builder;

import java.util.UUID;

@Builder
public class EnderecoBuilder {

    @Builder.Default
    private UUID id = UUID.fromString("ed1350db-2bc0-4ec0-a0a8-5efa624b6e3f");
    @Builder.Default
    private String logradouro = "Bairro Morro do Sol, Rua Lagoa da Prata";
    @Builder.Default
    private String cep = "35680286";
    @Builder.Default
    private String numero = "72";
    @Builder.Default
    private String cidade = "Itaúna";
    @Builder.Default
    private Boolean ativo = false;

    @Builder.Default
    private Pessoa pessoa = PessoaBuilder.builder().build().toPessoa();
    public Endereco toEndereco() {
        return new Endereco(id,
                logradouro,
                cep,
                numero,
                cidade,
                ativo,
                pessoa);
    }
}
