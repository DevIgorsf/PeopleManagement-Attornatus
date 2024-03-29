package com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    private final EnderecoRepository enderecoRepository;

    @Autowired
    public PessoaService(PessoaRepository pessoaRepository, EnderecoRepository enderecoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.enderecoRepository = enderecoRepository;
    }

    public PessoaDto criarPessoa(PessoaCadastro pessoaCastro) {
        Pessoa pessoa = new Pessoa(pessoaCastro);

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

    public List<PessoaDto> listarPessoas() {
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

    public List<EnderecoDto> listaEndereco(UUID id) {
        Optional<Pessoa> pessoaOptional = pessoaRepository.findById(id);

        if( pessoaOptional.isEmpty()) {
            throw new EntityNotFoundException("Pessoa não encontrada");
        }

        return pessoaOptional.get().getEnderecoLista().stream().map(EnderecoDto::new).toList();
    }

    public List<EnderecoDto> ativaEndereco(UUID id, UUID enderecoId) {
        Optional<Pessoa> pessoaOptional = pessoaRepository.findById(id);

        if(pessoaOptional.isEmpty()) {
            throw new EntityNotFoundException("Pessoa não encontrada");
        }

        if(!pessoaOptional.get().buscaEndereco(enderecoId)) {
            throw new EntityNotFoundException("Endereço não encontrado");
        }

        pessoaOptional.get().ativaEndereco(enderecoId);

        return pessoaOptional.get().getEnderecoLista().stream().map(EnderecoDto::new).toList();

    }

    public EnderecoDto atualizaEndereco(UUID id, UUID enderecoId, EnderecoAtualizacao form) {
        Optional<Pessoa> pessoaOptional = pessoaRepository.findById(id);

        if(pessoaOptional.isEmpty()) {
            throw new EntityNotFoundException("Pessoa não encontrada");
        }

        if(!pessoaOptional.get().buscaEndereco(enderecoId)) {
            throw new EntityNotFoundException("Endereço não encontrado");
        }

        if(enderecoRepository.findById(enderecoId).isEmpty()) {
            throw new EntityNotFoundException("Endereço não encontrado");
        }
        Endereco endereco = enderecoRepository.findById(enderecoId).get();
        endereco.atualizarInformacoes(form);

        return new EnderecoDto(endereco);
    }
}
