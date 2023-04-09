package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.Endereco;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PessoaService {

    private PessoaRepository pessoaRepository;

    private EnderecoRepository enderecoRepository;

    @Autowired
    public PessoaService(PessoaRepository pessoaRepository, EnderecoRepository enderecoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.enderecoRepository = enderecoRepository;
    }

    public PessoaDto criarPessoa(PessoaCadastro pessoaForm) {
        Pessoa pessoa = new Pessoa(pessoaForm);

        pessoaRepository.save(pessoa);

        return new PessoaDto(pessoa);
    }

    public PessoaDto buscarPessoa(UUID id) {
        Optional<Pessoa> usuario = pessoaRepository.findById(id);

        if( usuario.isEmpty()) {
            throw new EntityNotFoundException("Pessoa não encontrada");
        }

        return new PessoaDto(usuario.get());
    }

    public PessoaDto editarPessoa(PessoaAtualizacao form) {
        Optional<Pessoa> pessoaOptional = pessoaRepository.findById(form.id());

        if( pessoaOptional.isEmpty()) {
            throw new EntityNotFoundException("Pessoa não encontrada");
        }

        Pessoa pessoa = pessoaOptional.get();
        pessoa.atualizarInformacoes(form);

        return new PessoaDto(pessoa);
    }

    public List<PessoaDto> ListarPessoas() {
        List<PessoaDto> pessoaLista = pessoaRepository.findAll().stream().map(PessoaDto::new).toList();

        if(pessoaLista.isEmpty()) {
            throw new EntityNotFoundException("Não há pessoas cadastradas");
        }

        return pessoaLista;
    }

    public PessoaDto buscarPessoaPorNome(String nome) {
        Optional<Pessoa> pessoaOptional = pessoaRepository.findByNome(nome);

        if(pessoaOptional.isEmpty()){
            throw new EntityNotFoundException("Pessoa não encontrada");
        }
        Pessoa pessoa = pessoaOptional.get();

        return new PessoaDto(pessoa);
    }

    public EnderecoDto criarEndereco(UUID id, EnderecoForm form) {
        Optional<Pessoa> pessoaOptional = pessoaRepository.findById(id);

        if(pessoaOptional.isEmpty()){
            throw new EntityNotFoundException("Pessoa não encontrada");
        }
        Pessoa pessoa = pessoaOptional.get();

        Endereco enderecoCriado = new Endereco(form);
        enderecoCriado.setPessoa(pessoa);

        Endereco enderecoSalvo = enderecoRepository.save(enderecoCriado);

        return new EnderecoDto(enderecoSalvo);
    }

    public List<EnderecoDto> ListaEndereco(UUID id) {
        Optional<Pessoa> pessoaOptional = pessoaRepository.findById(id);

        if( pessoaOptional.isEmpty()) {
            throw new EntityNotFoundException("Pessoa não encontrada");
        }

        List<EnderecoDto> listaEndereco = pessoaOptional.get().getEnderecoLista().stream().map(EnderecoDto::new).toList();

        return listaEndereco;
    }

    public List<EnderecoDto> AtivaEndereco(UUID id, UUID enderecoId) {
        Optional<Pessoa> pessoaOptional = pessoaRepository.findById(id);

        if(pessoaOptional.isEmpty()) {
            throw new EntityNotFoundException("Pessoa não encontrada");
        }

        if(!pessoaOptional.get().buscaEndereco(enderecoId)) {
            throw new EntityNotFoundException("Endereço não encontrado");
        }

        pessoaOptional.get().ativaEndereco(enderecoId);

        List<EnderecoDto> listaEndereco = pessoaOptional.get().getEnderecoLista().stream().map(EnderecoDto::new).toList();

        return listaEndereco;
    }
}
