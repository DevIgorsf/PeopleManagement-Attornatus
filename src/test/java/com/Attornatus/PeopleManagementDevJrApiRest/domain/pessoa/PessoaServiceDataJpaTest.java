package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import com.Attornatus.PeopleManagementDevJrApiRest.builder.EnderecoFormBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.builder.PessoaCadastroBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PessoaServiceDataJpaTest {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaService pessoaService;


    @TestConfiguration
    static class TestConfig {
        @Bean
        public PessoaService pessoaService(
                PessoaRepository pessoaRepository,
                EnderecoRepository enderecoRepository
        ) {
            return new PessoaService(
                    pessoaRepository, enderecoRepository
            );
        }
    }

    @Test
    @DisplayName("Teste de integração para criar endereço")
    public void testCriarEndereco_PessoaExistente() {

        PessoaCadastro pessoaCadastro = PessoaCadastroBuilder.builder().build().toPessoaCadastro();
        PessoaDto pessoaDto = pessoaService.criarPessoa(pessoaCadastro);

        EnderecoForm form = EnderecoFormBuilder.builder().build().toEnderecoForm();
        UUID pessoaId = pessoaDto.id();

        EnderecoDto enderecoDto = pessoaService.criarEndereco(pessoaId, form);

        assertNotNull(enderecoDto);

    }
}
