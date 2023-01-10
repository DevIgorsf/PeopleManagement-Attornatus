package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PessoaDto(
    UUID id,
    String nome,
    LocalDate nascimento,
    List<EnderecoDto> enderecoLista) {

    public PessoaDto(Pessoa usuario) {
        this(usuario.getId(),
                usuario.getNome(),
                usuario.getNascimento(),
                usuario.getEnderecoLista().stream().map(EnderecoDto::new).toList());
    }
}
