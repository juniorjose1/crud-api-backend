package com.alexandre.crudapi.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alexandre.crudapi.event.RecursoCriadoEvent;
import com.alexandre.crudapi.model.Pessoa;
import com.alexandre.crudapi.repository.PessoaRepository;
import com.alexandre.crudapi.repository.filter.PessoaFilter;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/pessoas")
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Pessoa> pesquisar(PessoaFilter pessoaFilter){
		return pessoaRepository.filtrar(pessoaFilter);	
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Pessoa> listar(@PathVariable Long id){
		Optional<Pessoa> buscarPessoa = pessoaRepository.findById(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(buscarPessoa.get());
	}
	
	@PostMapping
	public ResponseEntity<Pessoa> cadastrar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response){
		Pessoa cadastrarPessoa = pessoaRepository.save(pessoa);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, cadastrarPessoa.getId()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(cadastrarPessoa);
		
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long id){
		pessoaRepository.deleteById(id);	
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Pessoa> alterar(@PathVariable Long id, @Valid @RequestBody Pessoa pessoa){
		Optional<Pessoa> alterarPessoa = pessoaRepository.findById(id);
		BeanUtils.copyProperties(pessoa, alterarPessoa.get(), "id");
		pessoaRepository.save(alterarPessoa.get());
		return ResponseEntity.status(HttpStatus.OK).body(alterarPessoa.get());
	}

}
