package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import com.Attornatus.PeopleManagementDevJrApiRest.builder.*;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.Endereco;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private PessoaService pessoaService;

    @Test
    void criarPessoa() {
        PessoaCadastro pessoaCadastro = PessoaCadastroBuilder.builder().build().toPessoaCadastro();
        Pessoa pessoa = new Pessoa(pessoaCadastro);
        Pessoa pessoaSalva = PessoaBuilder.builder().build().toPessoa();

        given(pessoaRepository.save(pessoa)).willReturn(pessoaSalva);

        PessoaDto pessoaDto = pessoaService.criarPessoa(pessoaCadastro);

        verify(pessoaRepository, times(1)).save(any(Pessoa.class));

        // Verificar se o retorno do serviço é o esperado
        assertNotNull(pessoaDto);
        assertEquals(pessoaSalva.getNome(), pessoaDto.nome());
        assertEquals(pessoaSalva.getNascimento(), pessoaDto.nascimento());
        assertNotNull(pessoaDto.enderecoLista());
        assertEquals(0, pessoaDto.enderecoLista().size()); // Espera-se que a lista de endereços esteja vazia

    }

    @Test
    void buscarPessoa() {
        UUID id = UUID.fromString("d1e00b34-6895-45b8-b884-6e41975b0580");
        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setNome("Giovanna");
        pessoa.setNascimento(LocalDate.parse("2001-03-09"));

        when(pessoaRepository.findById(id)).thenReturn(Optional.of(pessoa));

        // Quando
        PessoaDto pessoaDto = pessoaService.buscarPessoa(id);

        // Então
        assertNotNull(pessoaDto);
        assertEquals(id, pessoaDto.id());
        assertEquals("Giovanna", pessoaDto.nome());
        assertEquals(LocalDate.parse("2001-03-09"), pessoaDto.nascimento());

    }

    @Test
    public void testBuscarPessoaNaoEncontrada() {
        // Dado
        UUID id = UUID.fromString("d1e00b34-6895-45b8-b884-6e41975b0580");

        when(pessoaRepository.findById(id)).thenReturn(Optional.empty());

        // Então
        assertThrows(EntityNotFoundException.class, () -> pessoaService.buscarPessoa(id));
    }

    @Test
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
    public void testEditarPessoa_NaoExistente() {
        // Dado que o repository retorna um Optional vazio, indicando que a Pessoa não foi encontrada
        UUID pessoaId = UUID.randomUUID();
        PessoaAtualizacao form = new PessoaAtualizacao(pessoaId, "Novo Nome", LocalDate.now());
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.empty());

        // Quando o método editarPessoa é chamado com um ID inválido
        // Então uma EntityNotFoundException deve ser lançada
        assertThrows(EntityNotFoundException.class, () -> pessoaService.editarPessoa(form));

        // E o método findById do repository deve ter sido chamado uma vez com o ID correto
        verify(pessoaRepository, times(1)).findById(pessoaId);
    }

    @Test
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
    public void testListarPessoas_PessoasNaoExiste() {
        when(pessoaRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> pessoaService.listarPessoas());

        verify(pessoaRepository, times(1)).findAll();
    }

    @Test
    public void testBuscarPessoaPorNome_Existente() {
        // Dado que o repository retorna um Optional contendo uma Pessoa existente
        String nome = "João";
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        when(pessoaRepository.findByNome(nome)).thenReturn(Optional.of(pessoa));

        // Quando o método buscarPessoaPorNome é chamado com um nome válido
        PessoaDto pessoaDto = pessoaService.buscarPessoaPorNome(nome);

        // Então o resultado não deve ser nulo
        assertNotNull(pessoaDto);
        // E o resultado deve ter o mesmo nome e informações da Pessoa encontrada
        assertEquals(pessoa.getNome(), pessoaDto.nome());
        // E o método findByNome do repository deve ter sido chamado uma vez com o nome correto
        verify(pessoaRepository, times(1)).findByNome(nome);
    }

    @Test
    public void testBuscarPessoaPorNome_NaoExistente() {
        // Dado que o repository retorna um Optional vazio, indicando que a Pessoa não foi encontrada
        String nome = "Maria";
        when(pessoaRepository.findByNome(nome)).thenReturn(Optional.empty());

        // Quando o método buscarPessoaPorNome é chamado com um nome inválido
        // Então uma EntityNotFoundException deve ser lançada
        assertThrows(EntityNotFoundException.class, () -> pessoaService.buscarPessoaPorNome(nome));

        // E o método findByNome do repository deve ter sido chamado uma vez com o nome correto
        verify(pessoaRepository, times(1)).findByNome(nome);
    }

    @Test
    public void testCriarEndereco_PessoaNaoExistente() {
        // Dado que o repository retorna um Optional vazio, indicando que a Pessoa não foi encontrada
        UUID pessoaId = UUID.randomUUID();
        EnderecoForm form = new EnderecoForm("Rua A", "12345678", "123", "Cidade A");
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.empty());

        // Quando o método criarEndereco é chamado com um ID de Pessoa inválido
        // Então uma EntityNotFoundException deve ser lançada
        assertThrows(EntityNotFoundException.class, () -> pessoaService.criarEndereco(pessoaId, form));

        // E o método findById do repository de Pessoa deve ter sido chamado uma vez com o ID de Pessoa correto
        verify(pessoaRepository, times(1)).findById(pessoaId);
        // E o método save do repository de Endereco não deve ter sido chamado
        verifyNoInteractions(enderecoRepository);
    }

    @Test
    public void testListaEndereco_PessoaExistente() {
        // Dado que o repository retorna um Optional contendo uma Pessoa existente
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

        // Quando o método listaEndereco é chamado com um ID de Pessoa válido
        List<EnderecoDto> listaEnderecoDto = pessoaService.listaEndereco(pessoaId);

        // Então o resultado não deve ser nulo
        assertNotNull(listaEnderecoDto);
        // E o tamanho da lista deve ser igual ao tamanho da lista de Enderecos da Pessoa
        assertEquals(pessoa.getEnderecoLista().size(), listaEnderecoDto.size());
        // E o método findById do repository de Pessoa deve ter sido chamado uma vez com o ID de Pessoa correto
        verify(pessoaRepository, times(1)).findById(pessoaId);
    }

    @Test
    public void testListaEndereco_PessoaNaoExistente() {
        // Dado que o repository retorna um Optional vazio, indicando que a Pessoa não foi encontrada
        UUID pessoaId = UUID.randomUUID();
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.empty());

        // Quando o método listaEndereco é chamado com um ID de Pessoa inválido
        // Então uma EntityNotFoundException deve ser lançada
        assertThrows(EntityNotFoundException.class, () -> pessoaService.listaEndereco(pessoaId));

        // E o método findById do repository de Pessoa deve ter sido chamado uma vez com o ID de Pessoa correto
        verify(pessoaRepository, times(1)).findById(pessoaId);
    }

    @Test
    public void testAtivaEndereco_PessoaComEnderecoAtivo() {
        // Criação dos dados de teste
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

        // Configuração do comportamento do mock do PessoaRepository
        when(pessoaRepository.findById(pessoa.getId())).thenReturn(Optional.of(pessoa));

        // Chamada ao método a ser testado
        List<EnderecoDto> listaEndereco = pessoaService.ativaEndereco(pessoa.getId(), endereco1.getId());

        // Verificações de resultado
        assertNotNull(listaEndereco);
        assertEquals(2, listaEndereco.size()); // Verifica se retornou dois endereços

        // Verifica se o endereço 1 foi ativado e o endereço 2 foi desativado
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

        // Verificações de interação com o PessoaRepository
        verify(pessoaRepository, times(1)).findById(pessoa.getId());
    }

    @Test
    public void testAtivaEndereco_PessoaNaoExiste() {
        UUID pessoaId = UUID.randomUUID();
        when(pessoaRepository.findById(pessoaId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pessoaService.ativaEndereco(pessoaId, UUID.randomUUID()));

        verify(pessoaRepository, times(1)).findById(pessoaId);
    }

    @Test
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