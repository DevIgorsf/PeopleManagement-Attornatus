package com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.Pessoa;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "char(36)")
    private UUID id;
    private String logradouro;
    private String cep;
    private String numero;
    private String cidade;

    private Boolean ativo;

    @ManyToOne
    @JsonBackReference
    private Pessoa pessoa;

    public Endereco(EnderecoForm dados) {
        this.logradouro = dados.logradouro();
        this.cep = dados.cep();
        this.cidade = dados.cidade();
        this.numero = dados.numero();
        this.ativo = false;
    }

    public void ativarEndereco(boolean ativo) {
        this.ativo = ativo;
    }

    public void atualizarInformacoes(EnderecoAtualizacao dados) {
        if (dados.logradouro() != null) {
            this.logradouro = dados.logradouro();
        }
        if (dados.cep() != null) {
            this.cep = dados.cep();
        }
        if (dados.cidade() != null) {
            this.cidade = dados.cidade();
        }
        if (dados.numero() != null) {
            this.numero = dados.numero();
        }
    }
}
