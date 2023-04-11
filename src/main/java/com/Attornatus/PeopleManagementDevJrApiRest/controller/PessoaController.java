package com.Attornatus.PeopleManagementDevJrApiRest.controller;

import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoAtualizacao;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.endereco.EnderecoForm;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaAtualizacao;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaCadastro;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaDto;
import com.Attornatus.PeopleManagementDevJrApiRest.domain.pessoa.PessoaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pessoa")
public class PessoaController {

    private final PessoaService pessoaService;

    @Autowired
    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PessoaDto> criarPessoa(@RequestBody @Valid PessoaCadastro form) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaService.criarPessoa(form));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaDto> buscarPessoa(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(pessoaService.buscarPessoa(id));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<PessoaDto> buscarPessoaPorNome(@PathVariable String nome) {
        return ResponseEntity.status(HttpStatus.OK).body(pessoaService.buscarPessoaPorNome(nome));
    }

    @GetMapping
    public ResponseEntity<List<PessoaDto>> listarPessoas() {
        return ResponseEntity.status(HttpStatus.OK).body(pessoaService.listarPessoas());
    }

    @PutMapping
    @Transactional
    public ResponseEntity<PessoaDto> editarPessoa(@RequestBody @Valid PessoaAtualizacao form) {
        return ResponseEntity.status(HttpStatus.OK).body(pessoaService.editarPessoa(form));
    }

    @PostMapping("/{id}/endereco")
    @Transactional
    public ResponseEntity<EnderecoDto> criarEndereco(@PathVariable UUID id , @RequestBody @Valid EnderecoForm form) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaService.criarEndereco(id, form));
    }

    @PutMapping("/{id}/endereco/{enderecoId}")
    @Transactional
    public ResponseEntity<EnderecoDto> atualizarEndereco(
            @PathVariable UUID id , @PathVariable UUID enderecoId, @RequestBody @Valid EnderecoAtualizacao form) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaService.atualizaEndereco(id, enderecoId, form));
    }

    @PostMapping("/{id}/ativaEndereco/{enderecoId}")
    @Transactional
    public ResponseEntity<List<EnderecoDto>> ativaEndereco(@PathVariable UUID id, @PathVariable UUID enderecoId) {
        return ResponseEntity.status(HttpStatus.OK).body(pessoaService.ativaEndereco(id, enderecoId));
    }

    @GetMapping("/{id}/endereco")
    public ResponseEntity<List<EnderecoDto>> listaEndereco(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(pessoaService.listaEndereco(id));
    }
}
