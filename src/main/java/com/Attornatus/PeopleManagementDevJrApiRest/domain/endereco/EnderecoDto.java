package com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco;

import java.util.UUID;

public record EnderecoDto(

        UUID id,
        String logradouro,
        String cep,
        String numero,
        String cidade,
        Boolean ativo) {
    public EnderecoDto(Endereco enderecoSalvo) {
        this(enderecoSalvo.getId(),
                enderecoSalvo.getLogradouro(),
                enderecoSalvo.getCep(),
                enderecoSalvo.getNumero(),
                enderecoSalvo.getCidade(),
                enderecoSalvo.getAtivo()
        );

    }
}
