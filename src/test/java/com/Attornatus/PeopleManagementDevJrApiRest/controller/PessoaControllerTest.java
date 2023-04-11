package com.Attornatus.PeopleManagementDevJrApiRest.controller;

import com.Attornatus.PeopleManagementDevJrApiRest.builder.EnderecoDtoBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.builder.EnderecoFormBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.builder.PessoaCadastroBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.builder.PessoaDtoBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaAtualizacao;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaCadastro;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @DisplayName("Teste para criar pessoa")
    @WithMockUser
    void criaPessoaDadosValidos() throws Exception {
        PessoaCadastro pessoaCadastro = PessoaCadastroBuilder.builder().build().toPessoaCadastro();
        PessoaDto pessoaDto = PessoaDtoBuilder.builder().build().toPessoaDto();
        List<EnderecoDto> listaEnderecoDto = Collections.singletonList(EnderecoDtoBuilder.builder().build().toEnderecoDto());

        given(pessoaService.criarPessoa(pessoaCadastro)).willReturn(pessoaDto);

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
    @DisplayName("Teste para validar mensagem de erro ao criar pessoa com dados inválidos")
    @WithMockUser
    void criaPessoaDadosInvalidos() throws Exception {
        mockMvc.perform(post(PESSOA_API_URL_PATH))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Teste para buscar pessoa por id")
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
    @DisplayName("Teste para validar mensagem de erro ao buscar pessoa por id inválido")
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
    @DisplayName("Teste para buscar pessoa por nome")
    @WithMockUser
    void buscarPessoaPorNome() throws Exception {
        PessoaDto pessoaDto = PessoaDtoBuilder.builder().build().toPessoaDto();
        String nome = "Giovanna Dafne";

        given(pessoaService.buscarPessoaPorNome(nome))
                .willReturn(pessoaDto);

        mockMvc.perform(get(PESSOA_API_URL_PATH + "/nome/" + nome))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(pessoaDto.id().toString()))
                .andExpect(jsonPath("nome").value(pessoaDto.nome()))
                .andExpect(jsonPath("nascimento").value(pessoaDto.nascimento().toString()));
    }

    @Test
    @DisplayName("Teste para validar mensagem de erro ao buscar pessoa por nome de pessoa não existente")
    @WithMockUser
    void buscarPessoaPorNomeInvalido() throws Exception {
        String nome = "Giovanna Dafne";
        given(pessoaService.buscarPessoaPorNome(nome))
                .willThrow(new EntityNotFoundException("Pessoa não encontrada"));

        mockMvc.perform(get(PESSOA_API_URL_PATH + "/nome/" + nome))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Pessoa não encontrada"));
    }

    @Test
    @DisplayName("Teste para validar retorno ao listar pessoas")
    @WithMockUser
    void listarPessoas() throws Exception {
        PessoaDto pessoaDto = PessoaDtoBuilder.builder().build().toPessoaDto();

        given(pessoaService.listarPessoas())
                .willReturn(Collections.singletonList(pessoaDto));

        mockMvc.perform(get(PESSOA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(pessoaDto.id().toString()))
                .andExpect(jsonPath("$[0].nome").value(pessoaDto.nome()))
                .andExpect(jsonPath("$[0].nascimento").value(pessoaDto.nascimento().toString()));
    }

    @Test
    @DisplayName("Teste para validar mensagem de erro ao listar pessoa com banco de dados vázio")
    @WithMockUser
    void listarPessoasSemPessoasCadastradas() throws Exception {
        given(pessoaService.listarPessoas())
                .willThrow(new EntityNotFoundException("Não há pessoas cadastradas"));

        mockMvc.perform(get(PESSOA_API_URL_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Não há pessoas cadastradas"));
    }

    @Test
    @DisplayName("Teste para validar retorno ao editar pessoa")
    @WithMockUser
    void editarPessoa() throws Exception {
        EnderecoDto enderecoDto = EnderecoDtoBuilder.builder().build().toEnderecoDto();
        PessoaAtualizacao pessoaAtualizacao = new PessoaAtualizacao(
                UUID.fromString("0a692751-a7ce-4dd1-ab80-55f9e653de75"),
                "Novo Nome",
                LocalDate.of(1997, 1, 23));

        when(pessoaService.editarPessoa(any(PessoaAtualizacao.class)))
                .thenReturn(new PessoaDto(
                        pessoaAtualizacao.id(),
                        pessoaAtualizacao.nome(),
                        pessoaAtualizacao.nascimento(),
                        List.of(enderecoDto)));

        mockMvc.perform(put(PESSOA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoaAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"));

        verify(pessoaService, times(1)).editarPessoa(eq(pessoaAtualizacao));
    }

    @Test
    @DisplayName("Teste para validar mensagem de erro ao tentar editar pessoa com id invalido")
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
    @DisplayName("Teste para criar endereço")
    @WithMockUser
    void criarEndereco() throws Exception {
        EnderecoForm enderecoForm = EnderecoFormBuilder.builder().build().toEnderecoForm();

        when(pessoaService.criarEndereco(any(UUID.class), any(EnderecoForm.class)))
                .thenReturn(new EnderecoDto(
                        UUID.randomUUID(),
                        enderecoForm.logradouro(),
                        enderecoForm.cep(),
                        enderecoForm.numero(),
                        enderecoForm.cidade(),
                        true));

        mockMvc.perform(post(PESSOA_API_URL_PATH + "/" + UUID.randomUUID() + "/endereco") // Endpoint do seu controller
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoForm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.logradouro").value("Bairro Morro do Sol, Rua Lagoa da Prata"));

        verify(pessoaService, times(1)).criarEndereco(any(UUID.class), eq(enderecoForm));
    }

    @Test
    @DisplayName("Teste para validar mensagem de erro ao tentar criar com id invalido")
    @WithMockUser
    void criarEnderecoParaIdInvalido() throws Exception {
        UUID pessoaId = UUID.randomUUID();
        EnderecoForm enderecoForm = EnderecoFormBuilder.builder().build().toEnderecoForm();

        given(pessoaService.criarEndereco(pessoaId, enderecoForm))
                .willThrow(new EntityNotFoundException("Pessoa não encontrada"));

        mockMvc.perform(post(PESSOA_API_URL_PATH + "/" + pessoaId + "/endereco")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enderecoForm)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Pessoa não encontrada"));
    }

    @Test
    @DisplayName("Teste para listar endereços")
    @WithMockUser
    void listaEndereco() throws Exception{
        // Dado
        UUID pessoaId = UUID.randomUUID();
        EnderecoDto enderecoDto1 = new EnderecoDto(UUID.fromString("028683c6-0eff-4cbf-8cc6-33200ba425b2"),
        "Bairro Morro do Sol, Rua Lagoa da Prata", "35680286", "72", "Itaúna", false);
        EnderecoDto enderecoDto2 = new EnderecoDto(UUID.fromString("7e51778e-048a-4296-aa49-1831466d159d"),
        "Bairro Res. Veredas, Rua Andressa", "35680151", "162", "Itaúna", false);
        List<EnderecoDto> enderecoDtoList = Arrays.asList(enderecoDto1, enderecoDto2);
        String response = objectMapper.writeValueAsString(enderecoDtoList);

        given(pessoaService.listaEndereco(pessoaId)).willReturn(enderecoDtoList);

        mockMvc.perform(get(PESSOA_API_URL_PATH + "/" + pessoaId + "/endereco")
                .param("id", "028683c6-0eff-4cbf-8cc6-33200ba425b2")
                .param("logradouro", "Bairro Morro do Sol, Rua Lagoa da Prata")
                .param("cep", "35680286")
                .param("numero", "162")
                .param("cidade", "Itaúna"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(response));

        verify(pessoaService, times(1)).listaEndereco(any(UUID.class));
    }

    @Test
    @DisplayName("Teste para validar mensagem de erro ao listar endereços com id da pessoa Inválido")
    @WithMockUser
    public void testeListaEnderecoPessoaNaoEncontrada() throws Exception {
        UUID id = UUID.randomUUID();

        when(pessoaService.listaEndereco(any(UUID.class)))
                .thenThrow(new EntityNotFoundException("Pessoa não encontrada"));

        mockMvc.perform(get(PESSOA_API_URL_PATH + "/"  + id + "/endereco"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Pessoa não encontrada"));

        verify(pessoaService, times(1)).listaEndereco(Mockito.eq(id));
    }

    @Test
    @DisplayName("Teste para ativar endereço")
    @WithMockUser
    void ativaEnderecoComPessoaExistenteEnderecoExistente() throws Exception {
        UUID pessoaId = UUID.randomUUID();
        UUID enderecoId = UUID.fromString("028683c6-0eff-4cbf-8cc6-33200ba425b2");
        EnderecoDto enderecoDto1 = new EnderecoDto(enderecoId,
                "Bairro Morro do Sol, Rua Lagoa da Prata", "35680286", "72", "Itaúna", true);
        EnderecoDto enderecoDto2 = new EnderecoDto(UUID.fromString("7e51778e-048a-4296-aa49-1831466d159d"),
                "Bairro Res. Veredas, Rua Andressa", "35680151", "162", "Itaúna", false);
        List<EnderecoDto> enderecoDtoList = Arrays.asList(enderecoDto1, enderecoDto2);
        String response = objectMapper.writeValueAsString(enderecoDtoList);
        given(pessoaService.ativaEndereco(pessoaId, enderecoId)).willReturn(enderecoDtoList);

        mockMvc.perform(
                post(PESSOA_API_URL_PATH + "/" + pessoaId + "/ativaEndereco/" + enderecoId)
                        .param("id", "028683c6-0eff-4cbf-8cc6-33200ba425b2")
                        .param("logradouro", "Bairro Morro do Sol, Rua Lagoa da Prata")
                        .param("cep", "35680286")
                        .param("numero", "162")
                        .param("cidade", "Itaúna")
                        .param("ativo", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(response));

        verify(pessoaService, times(1)).ativaEndereco(any(UUID.class), any(UUID.class));
    }


    @Test
    @DisplayName("Teste para validar mensagem de erro ao tentar ativar endereço com id de pessoa inválido")
    @WithMockUser
    void ativaEnderecoComPessoaInexistenteEnderecoExistente() throws Exception{
        UUID pessoaId = UUID.randomUUID();
        UUID enderecoId = UUID.fromString("028683c6-0eff-4cbf-8cc6-33200ba425b2");

        given(pessoaService.ativaEndereco(any(UUID.class), any(UUID.class)))
                .willThrow( new EntityNotFoundException("Pessoa não encontrada"));

        mockMvc.perform(
                        post(PESSOA_API_URL_PATH + "/" + pessoaId + "/ativaEndereco/" + enderecoId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("message").value("Pessoa não encontrada"));

        verify(pessoaService, times(1)).ativaEndereco(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("Teste para validar mensagem de erro ao tentar ativar endereço com id de endereço inválido")
    @WithMockUser
    void ativaEnderecoComPessoaExistenteEnderecoInexistente() throws Exception{
        UUID pessoaId = UUID.randomUUID();
        UUID enderecoId = UUID.fromString("028683c6-0eff-4cbf-8cc6-33200ba425b2");

        given(pessoaService.ativaEndereco(pessoaId, enderecoId)).willThrow( new EntityNotFoundException("Endereço não encontrado"));

        mockMvc.perform(
                        post(PESSOA_API_URL_PATH + "/" + pessoaId + "/ativaEndereco/" + enderecoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("Endereço não encontrado"));

        verify(pessoaService, times(1)).ativaEndereco(any(UUID.class), any(UUID.class));
    }
}