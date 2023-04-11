package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import com.Attornatus.PeopleManagementDevJrApiRest.builder.*;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.Endereco;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private PessoaService pessoaService;

    @Test
    @DisplayName("Teste para criar pessoa")
    void criarPessoa() {
        PessoaCadastro pessoaCadastro = PessoaCadastroBuilder.builder().build().toPessoaCadastro();
        Pessoa pessoa = new Pessoa(pessoaCadastro);
        Pessoa pessoaSalva = PessoaBuilder.builder().build().toPessoa();

        given(pessoaRepository.save(pessoa)).willReturn(pessoaSalva);

        PessoaDto pessoaDto = pessoaService.criarPessoa(pessoaCadastro);

        verify(pessoaRepository, times(1)).save(any(Pessoa.class));

        assertNotNull(pessoaDto);
        assertEquals(pessoaSalva.getNome(), pessoaDto.nome());
        assertEquals(pessoaSalva.getNascimento(), pessoaDto.nascimento());
        assertNotNull(pessoaDto.enderecoLista());
        assertEquals(0, pessoaDto.enderecoLista().size()); // Espera-se que a lista de endereços esteja vazia

    }

    @Test
    @DisplayName("Teste para buscar pessoa")
    void buscarPessoa() {
        UUID id = UUID.fromString("d1e00b34-6895-45b8-b884-6e41975b0580");
        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setNome("Giovanna");
        pessoa.setNascimento(LocalDate.parse("2001-03-09"));

        when(pessoaRepository.findById(id)).thenReturn(Optional.of(pessoa));

        PessoaDto pessoaDto = pessoaService.buscarPessoa(id);

        assertNotNull(pessoaDto);
        assertEquals(id, pessoaDto.id());
        assertEquals("Giovanna", pessoaDto.nome());
        assertEquals(LocalDate.parse("2001-03-09"), pessoaDto.nascimento());
    }

    @Test
    @DisplayName("Teste para validar envio de exception ao buscar pessoa não existente")
    public void testBuscarPessoaNaoEncontrada() {
        UUID id = UUID.fromString("d1e00b34-6895-45b8-b884-6e41975b0580");

        when(pessoaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pessoaService.buscarPessoa(id));
    }

    @Test
    @DisplayName("Teste para editar pessoa")
    public void testEditarPessoa_Existente() {
        // Dado que o repository retorna um Optional contendo uma Pessoa existente
        UUID pessoaId = UUID.randomUUID();
        Pessoa pessoa = new Pessoa();
        pessoa.setId(pessoaId);
        PessoaAtualizacao form = new PessoaAtualizacao(pessoaId, "Novo Nome", LocalDate.now());
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.of(pessoa));

        // Quando o método editarPessoa é chamado com um ID válido e uma PessoaAtualizacao válida
        PessoaDto pessoaDto = pessoaService.editarPessoa(form);

        // Então o resultado não deve ser nulo
        assertNotNull(pessoaDto);
        // E o resultado deve ter o mesmo ID e as informações atualizadas da Pessoa
        assertEquals(pessoa.getId(), pessoaDto.id());
        assertEquals(form.nome(), pessoaDto.nome());
        assertEquals(form.nascimento(), pessoaDto.nascimento());

        // E o método findById do repository deve ter sido chamado uma vez com o ID correto
        verify(pessoaRepository, times(1)).findById(pessoaId);
    }

    @Test
    @DisplayName("Teste para validar envio de exception ao tentar editar pessoa não existente")
    public void testEditarPessoa_NaoExistente() {
        UUID pessoaId = UUID.randomUUID();
        PessoaAtualizacao form = new PessoaAtualizacao(pessoaId, "Novo Nome", LocalDate.now());
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pessoaService.editarPessoa(form));

        verify(pessoaRepository, times(1)).findById(pessoaId);
    }

    @Test
    @DisplayName("Teste para listar pessoas")
    void testListarPessoas() {
        PessoaDto pessoaDto = PessoaDtoBuilder.builder().build().toPessoaDto();
        Pessoa pessoa = new Pessoa();
        pessoa.setId(pessoaDto.id());
        pessoa.setNome(pessoaDto.nome());
        pessoa.setNascimento(pessoaDto.nascimento());

        when(pessoaRepository.findAll()).thenReturn(Collections.singletonList(pessoa));

        List<PessoaDto> listaPessoaDto = pessoaService.listarPessoas();

        assertFalse(listaPessoaDto.isEmpty());
    }

    @Test
    @DisplayName("Teste para validar exception ao tentar lista pessoas com o banco de dados vázio")
    public void testListarPessoas_PessoasNaoExiste() {
        when(pessoaRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> pessoaService.listarPessoas());

        verify(pessoaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Teste para buscar pessoa por nome")
    public void testBuscarPessoaPorNome() {
        String nome = "João";
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        when(pessoaRepository.findByNome(nome)).thenReturn(Optional.of(pessoa));

        PessoaDto pessoaDto = pessoaService.buscarPessoaPorNome(nome);

        assertNotNull(pessoaDto);
        assertEquals(pessoa.getNome(), pessoaDto.nome());
        verify(pessoaRepository, times(1)).findByNome(nome);
    }

    @Test
    @DisplayName("Teste para validar exception ao tentar buscar pessoa por nome não existente")
    public void testBuscarPessoaPorNomeNaoExistente() {
        String nome = "Maria";
        when(pessoaRepository.findByNome(nome)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pessoaService.buscarPessoaPorNome(nome));

        verify(pessoaRepository, times(1)).findByNome(nome);
    }

    @Test
    @DisplayName("Teste para validar exception ao tentar criar endereço para pessoa não existente")
    public void testCriarEndereco_PessoaNaoExistente() {
        UUID pessoaId = UUID.randomUUID();
        EnderecoForm form = EnderecoFormBuilder.builder().build().toEnderecoForm();
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pessoaService.criarEndereco(pessoaId, form));

        verify(pessoaRepository, times(1)).findById(pessoaId);
        verifyNoInteractions(enderecoRepository);
    }

    @Test
    @DisplayName("Teste para listar endereços")
    public void testListaEndereco() {
        UUID pessoaId = UUID.randomUUID();
        Pessoa pessoa = new Pessoa();
        Endereco endereco1 = new Endereco();
        endereco1.setId(UUID.randomUUID());
        endereco1.setLogradouro("Rua A");
        endereco1.setCep("12345-678");
        endereco1.setNumero("123");
        endereco1.setCidade("São Paulo");
        endereco1.setAtivo(true);
        Endereco endereco2 = new Endereco();
        endereco2.setId(UUID.randomUUID());
        endereco2.setLogradouro("Rua B");
        endereco2.setCep("98765-432");
        endereco2.setNumero("456");
        endereco2.setCidade("Rio de Janeiro");
        endereco2.setAtivo(false);
        pessoa.getEnderecoLista().add(endereco1);
        pessoa.getEnderecoLista().add(endereco2);
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.of(pessoa));

        List<EnderecoDto> listaEnderecoDto = pessoaService.listaEndereco(pessoaId);

        assertNotNull(listaEnderecoDto);
        assertEquals(pessoa.getEnderecoLista().size(), listaEnderecoDto.size());
        verify(pessoaRepository, times(1)).findById(pessoaId);
    }

    @Test
    @DisplayName("Teste para validar exception ao tentar listar endereços para pessoa não existente")
    public void testListaEndereco_PessoaNaoExistente() {
        UUID pessoaId = UUID.randomUUID();
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pessoaService.listaEndereco(pessoaId));

        verify(pessoaRepository, times(1)).findById(pessoaId);
    }

    @Test
    @DisplayName("Teste para ativar endereço e verificar se o anterior foi desativado")
    public void testAtivaEndereco_PessoaComEnderecoAtivo() {
        UUID id = UUID.fromString("d1e00b34-6895-45b8-b884-6e41975b0580");
        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setNome("Giovanna");
        pessoa.setNascimento(LocalDate.parse("2001-03-09"));
        UUID enderecoId1 = UUID.randomUUID();
        UUID enderecoId2 = UUID.randomUUID();
        Endereco endereco1 = new Endereco(new EnderecoForm("Bairro Morro do Sol, Rua Lagoa da Prata", "35680286", "72", "Itaúna"));
        Endereco endereco2 = new Endereco(new EnderecoForm( "Bairro Res. Veredas, Rua Andressa", "35680151", "162", "Itaúna"));
        endereco1.setId(enderecoId1);
        endereco2.setId(enderecoId2);
        endereco1.setAtivo(false);
        endereco2.setAtivo(true);
        pessoa.adicionarEndereco(endereco1);
        pessoa.adicionarEndereco(endereco2);

        when(pessoaRepository.findById(pessoa.getId())).thenReturn(Optional.of(pessoa));

        List<EnderecoDto> listaEndereco = pessoaService.ativaEndereco(pessoa.getId(), endereco1.getId());

        assertNotNull(listaEndereco);
        assertEquals(2, listaEndereco.size());

        EnderecoDto endereco1Atualizado = listaEndereco.get(0);
        assertEquals(endereco1.getId(), endereco1Atualizado.id());
        assertEquals("Bairro Morro do Sol, Rua Lagoa da Prata", endereco1Atualizado.logradouro());
        assertEquals("35680286", endereco1Atualizado.cep());
        assertEquals("72", endereco1Atualizado.numero());
        assertEquals("Itaúna", endereco1Atualizado.cidade());
        assertTrue(endereco1Atualizado.ativo());

        EnderecoDto endereco2Atualizado = listaEndereco.get(1);
        assertEquals(endereco2.getId(), endereco2Atualizado.id());
        assertEquals("Bairro Res. Veredas, Rua Andressa", endereco2Atualizado.logradouro());
        assertEquals("35680151", endereco2Atualizado.cep());
        assertEquals("162", endereco2Atualizado.numero());
        assertEquals("Itaúna", endereco2Atualizado.cidade());
        assertFalse(endereco2Atualizado.ativo());

        verify(pessoaRepository, times(1)).findById(pessoa.getId());
    }

    @Test
    @DisplayName("Teste para validar exception ao tentar ativar endereço para pessoa não existente")
    public void testAtivaEndereco_PessoaNaoExiste() {
        UUID pessoaId = UUID.randomUUID();
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pessoaService.ativaEndereco(pessoaId, UUID.randomUUID()));

        verify(pessoaRepository, times(1)).findById(pessoaId);
    }

    @Test
    @DisplayName("Teste para validar exception ao tentar ativar endereço para endereço não existente")
    public void testAtivaEndereco_EnderecoNaoExiste() {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(UUID.randomUUID());
        Pessoa pessoaSpy = spy(pessoa);
        when(pessoaRepository.findById(pessoa.getId())).thenReturn(Optional.of(pessoa));

        UUID enderecoId = UUID.randomUUID();
        when(pessoaSpy.buscaEndereco(enderecoId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> pessoaService.ativaEndereco(pessoa.getId(), enderecoId));

        verify(pessoaRepository, times(1)).findById(pessoa.getId());
        verify(pessoaSpy, times(1)).buscaEndereco(enderecoId);
    }

}