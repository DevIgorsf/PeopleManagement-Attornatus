package com.Attornatus.PeopleManagementDevJrApiRest.builder;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import lombok.Builder;

@Builder
public class EnderecoFormBuilder {

    @Builder.Default
    private String logradouro = "Bairro Morro do Sol, Rua Lagoa da Prata";
    @Builder.Default
    private String cep = "35680286";
    @Builder.Default
    private String numero = "72";
    @Builder.Default
    private String cidade = "Itaúna";

    public EnderecoForm toEnderecoForm() {
        return new EnderecoForm(
                logradouro,
                cep,
                numero,
                cidade);
    }
}
