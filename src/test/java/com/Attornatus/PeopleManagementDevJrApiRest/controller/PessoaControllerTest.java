package com.Attornatus.PeopleManagementDevJrApiRest.controller;

import com.Attornatus.PeopleManagementDevJrApiRest.builder.EnderecoDtoBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.builder.PessoaCadastroBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.builder.PessoaDtoBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.Endereco;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.*;
import com.Attornatus.PeopleManagementDevJrApiRest.infra.exception.GlobalExceptions;
import com.Attornatus.PeopleManagementDevJrApiRest.infra.exception.StandardError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class PessoaControllerTest {

    private static final String PESSOA_API_URL_PATH = "/pessoa";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PessoaService pessoaService;

    @InjectMocks
    private PessoaController pessoaController;


    @Test
    @DisplayName("Deveria devolver codigo http 201 quando informaçôes estão validas")
    @WithMockUser
    void criaPessoaDadosValidos() throws Exception {
        // given
        PessoaCadastro pessoaCadastro = PessoaCadastroBuilder.builder().build().toPessoaForm();
        PessoaDto pessoaDto = PessoaDtoBuilder.builder().build().toPessoaDto();
        List<EnderecoDto> listaEnderecoDto = Collections.singletonList(EnderecoDtoBuilder.builder().build().toEnderecoDto());

        // when
        given(pessoaService.criarPessoa(pessoaCadastro)).willReturn(pessoaDto);

        // then
        var response = mockMvc.perform(post(PESSOA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new PessoaDto(
                                UUID.fromString("0a692751-a7ce-4dd1-ab80-55f9e653de75"),
                                "Giovanna Dafne",
                                LocalDate.parse("1997-01-23"),
                                listaEnderecoDto
                        )
                ))
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        var jsonEsperado = objectMapper.writeValueAsString(pessoaDto);

        assertThat(response.getContentAsString(StandardCharsets.UTF_8)).isEqualTo(jsonEsperado);
    }

    @Test
    @DisplayName("Deveria devolver codigo http 400 quando informações estão invalidas")
    @WithMockUser
    void criaPessoaDadosInvalidos() throws Exception {
        mockMvc.perform(post(PESSOA_API_URL_PATH))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 quando buscado o id de um usuário existente")
    @WithMockUser
    void buscarPessoaPorIdValido() throws Exception {
        PessoaDto pessoaDto = PessoaDtoBuilder.builder().build().toPessoaDto();
        UUID pessoaId = UUID.fromString("0a692751-a7ce-4dd1-ab80-55f9e653de75");

        given(pessoaService.buscarPessoa(pessoaId))
                .willReturn(pessoaDto);

        mockMvc.perform(get(PESSOA_API_URL_PATH + "/" + pessoaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(pessoaDto.id().toString()))
                .andExpect(jsonPath("nome").value(pessoaDto.nome()))
                .andExpect(jsonPath("nascimento").value(pessoaDto.nascimento().toString()));
    }

    @Test
    @DisplayName("Deveria devolver codigo http 404 quando o id não é encontrado e uma mensagem de erro")
    @WithMockUser
    void buscarPessoaPorIdInvalido() throws Exception {
        UUID idIvalido = UUID.randomUUID();
        given(pessoaService.buscarPessoa(idIvalido))
                .willThrow(new EntityNotFoundException("Pessoa não encontrada"));

        mockMvc.perform(get(PESSOA_API_URL_PATH + "/" + idIvalido))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Pessoa não encontrada"));

    }

    @Test
    void buscarPessoaPorNome() {
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 e uma lista de Pessoas")
    @WithMockUser
    void listarPessoas() throws Exception {
        PessoaDto pessoaDto = PessoaDtoBuilder.builder().build().toPessoaDto();

        given(pessoaService.ListarPessoas())
                .willReturn(Collections.singletonList(pessoaDto));

        mockMvc.perform(get(PESSOA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(pessoaDto.id().toString()))
                .andExpect(jsonPath("$[0].nome").value(pessoaDto.nome()))
                .andExpect(jsonPath("$[0].nascimento").value(pessoaDto.nascimento().toString()));
    }

    @Test
    @DisplayName("Deveria devolver codigo http 404 e uma mensagem de erro")
    @WithMockUser
    void listarPessoasSemPessoasCadastradas() throws Exception {
        given(pessoaService.ListarPessoas())
                .willThrow(new EntityNotFoundException("Não há pessoas cadastradas"));

        mockMvc.perform(get(PESSOA_API_URL_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Não há pessoas cadastradas"));
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 e dados do usuário que foi editado")
    @WithMockUser
    void editarPessoa() throws Exception {
        EnderecoDto enderecoDto = EnderecoDtoBuilder.builder().build().toEnderecoDto();
        PessoaAtualizacao pessoaAtualizacao = new PessoaAtualizacao(
                UUID.fromString("0a692751-a7ce-4dd1-ab80-55f9e653de75"),
                "Novo Nome",
                LocalDate.of(1997, 1, 23));

        // Configuração do comportamento do serviço mock
        when(pessoaService.editarPessoa(any(PessoaAtualizacao.class)))
                .thenReturn(new PessoaDto(
                        pessoaAtualizacao.id(),
                        pessoaAtualizacao.nome(),
                        pessoaAtualizacao.nascimento(),
                        List.of(enderecoDto)));

        // Execução da requisição PUT com o objeto atualizado
        mockMvc.perform(put(PESSOA_API_URL_PATH) // Endpoint do seu controller
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoaAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome")); // Verifica o nome no JSON de resposta

        // Verificação do comportamento esperado
        verify(pessoaService, times(1)).editarPessoa(eq(pessoaAtualizacao));
    }


    @Test
    @DisplayName("Deveria devolver codigo http 404 e uma mensagem de erro")
    @WithMockUser
    void editarPessoaComIdInvalido() throws Exception {
        PessoaAtualizacao pessoaAtualizacao = new PessoaAtualizacao(
                UUID.fromString("0a692751-a7ce-4dd1-ab80-55f9e653de75"),
                "Novo Nome",
                LocalDate.of(1997, 1, 23));
        given(pessoaService.editarPessoa(pessoaAtualizacao))
                .willThrow(new EntityNotFoundException("Pessoa não encontrada"));

        mockMvc.perform(put(PESSOA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pessoaAtualizacao)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Pessoa não encontrada"));
    }

    @Test
    @DisplayName("Deveria devolver codigo http 201 e dados do endereço criado")
    @WithMockUser
    void criarEndereco() throws Exception {
        // Criação do objeto de endereço a ser criado
        EnderecoForm enderecoForm = new EnderecoForm(
                "Rua dos Testes",
                "12345678",
                "123",
                "Teste City"
        );

        // Configuração do comportamento do serviço mock
        when(pessoaService.criarEndereco(any(UUID.class), any(EnderecoForm.class)))
                .thenReturn(new EnderecoDto(
                        UUID.randomUUID(),
                        enderecoForm.logradouro(),
                        enderecoForm.cep(),
                        enderecoForm.numero(),
                        enderecoForm.cidade(),
                        true));

        // Execução da requisição POST com o objeto de endereço
        mockMvc.perform(post(PESSOA_API_URL_PATH + "/" + UUID.randomUUID() + "/endereco") // Endpoint do seu controller
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoForm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.logradouro").value("Rua dos Testes")); // Verifica o logradouro no JSON de resposta

        // Verificação do comportamento esperado
        verify(pessoaService, times(1)).criarEndereco(any(UUID.class), eq(enderecoForm));
    }

    @Test
    @DisplayName("Deveria devolver codigo http 404 e mensagem de erro")
    @WithMockUser
    void criarEnderecoParaIdInvalido() throws Exception {
        // Configuração do comportamento do serviço mock
        given(pessoaService.criarEndereco(any(UUID.class), any(EnderecoForm.class)))
                .willThrow(new EntityNotFoundException("Pessoa não encontrada"));

        mockMvc.perform(post(PESSOA_API_URL_PATH + "/" + any(UUID.class) + "/endereco")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(any(EnderecoForm.class))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Pessoa não encontrada"));
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 com a lista de endereco")
    @WithMockUser
    void listaEndereco() throws Exception{
        // Dado
        EnderecoDto enderecoDto1 = new EnderecoDto(UUID.fromString("028683c6-0eff-4cbf-8cc6-33200ba425b2"),
        "Bairro Morro do Sol, Rua Lagoa da Prata", "35680286", "72", "Itaúna", false);
        EnderecoDto enderecoDto2 = new EnderecoDto(UUID.fromString("7e51778e-048a-4296-aa49-1831466d159d"),
        "Bairro Res. Veredas, Rua Andressa", "35680151", "162", "Itaúna", false);
        List<EnderecoDto> enderecoDtoList = Arrays.asList(enderecoDto1, enderecoDto2);

        String response = objectMapper.writeValueAsString(enderecoDtoList);

        given(pessoaService.ListaEndereco(any(UUID.class))).willReturn(enderecoDtoList);

        // Quando
        mockMvc.perform(get(PESSOA_API_URL_PATH + "/" + any(UUID.class) + "/endereco")
                .param("id", "028683c6-0eff-4cbf-8cc6-33200ba425b2")
                .param("logradouro", "Bairro Morro do Sol, Rua Lagoa da Prata")
                .param("cep", "35680286")
                .param("numero", "162")
                .param("cidade", "Itaúna"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(response));

        // Então
        verify(pessoaService, times(1)).ListaEndereco(any(UUID.class));
    }

    @Test
    @DisplayName("Deveria devolver codigo http 404 quando id é Invalido")
    @WithMockUser
    public void testeListaEnderecoPessoaNaoEncontrada() throws Exception {
        // Dado
        UUID id = UUID.randomUUID();

        when(pessoaService.ListaEndereco(Mockito.any(UUID.class)))
                .thenThrow(new EntityNotFoundException("Pessoa não encontrada"));

        // Quando
        mockMvc.perform(get(PESSOA_API_URL_PATH + "/"  + id + "/endereco"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Pessoa não encontrada"));

        // Então
        verify(pessoaService, Mockito.times(1)).ListaEndereco(Mockito.eq(id));
    }
}