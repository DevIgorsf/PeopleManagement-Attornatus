package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import com.Attornatus.PeopleManagementDevJrApiRest.builder.PessoaDtoBuilder;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.Endereco;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class PessoaServiceTest {

    @InjectMocks
    private PessoaService pessoaService;

    @MockBean
    private PessoaRepository pessoaRepository;


    @Test
    void criarPessoa() {


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
    void editarPessoa() {
    }

    @Test
    void listarPessoas() {

        PessoaDto pessoaDto = PessoaDtoBuilder.builder().build().toPessoaDto();
        Pessoa pessoa = new Pessoa();
        pessoa.setId(pessoaDto.id());
        pessoa.setNome(pessoaDto.nome());
        pessoa.setNascimento(pessoaDto.nascimento());

        when(pessoaRepository.findAll()).thenReturn(Collections.singletonList(pessoa));

        List<PessoaDto> listaPessoaDto = pessoaService.ListarPessoas();

        assertFalse(listaPessoaDto.isEmpty());

    }

    @Test
    void buscarPessoaPorNome() {
    }

    @Test
    void criarEndereco() {
    }

    @Test
    void listaEndereco() {
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
        List<EnderecoDto> listaEndereco = pessoaService.AtivaEndereco(pessoa.getId(), endereco1.getId());

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
        Mockito.verify(pessoaRepository, times(1)).findById(pessoa.getId());
        Mockito.verify(pessoaRepository, times(1)).save(pessoa);
    }
}