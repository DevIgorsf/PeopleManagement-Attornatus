package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.Endereco;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "pessoas")
@Entity(name = "Pessoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "char(36)")
    private UUID id;

    private String nome;

    private LocalDate nascimento;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Endereco> enderecoLista = new ArrayList<>();

    public Pessoa(PessoaCadastro pessoaCadastro) {
        this.nome = pessoaCadastro.nome();
        this.nascimento = pessoaCadastro.nascimento();
    }

    public void adicionarEndereco(Endereco endereco) {
        this.enderecoLista.add(endereco);
    }

    public void atualizarInformacoes(PessoaAtualizacao form) {
        if (form.nome() != null) {
            this.nome = form.nome();
        }
        if (form.nascimento() != null) {
            this.nascimento = form.nascimento();
        }
    }

    public void ativaEndereco(UUID id) {
        this.enderecoLista.forEach(
                endereco -> {
                    if(endereco.getId().equals(id)) {
                        endereco.ativarEndereco(true);
                    }else {
                        endereco.ativarEndereco(false);
                    }
                }
        );
    }

    public boolean buscaEndereco(UUID enderecoId) {
        for (Endereco endereco: this.getEnderecoLista()) {
            if(endereco.getId().equals(enderecoId)) {
                return true;
            }
        }
        return false;
    }
}
