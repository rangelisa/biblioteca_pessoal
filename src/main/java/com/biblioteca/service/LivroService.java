package com.biblioteca.service;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public List<Livro> listarTodos() {
        return repository.findAll();
    }

    public Optional<Livro> buscarPorId(String id) {
        return repository.findById(id);
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }

    public List<Livro> buscarPorAutor(String autor) {
        return repository.findByAutorContainingIgnoreCase(autor);
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        return repository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Livro> buscarPorLido(Boolean lido) {
        return repository.findByLido(lido);
    }

    public List<Livro> buscarPorGenero(String genero) {
        return repository.findByGeneroIgnoreCase(genero);
    }

    public Livro salvar(Livro livro) {
        if (repository.findByIsbn(livro.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Já existe um livro com este ISBN: " + livro.getIsbn());
        }
        return repository.save(livro);
    }

    public Livro atualizar(String id, Livro livroAtualizado) {
        return repository.findById(id).map(livro -> {
            livro.setTitulo(livroAtualizado.getTitulo());
            livro.setAutor(livroAtualizado.getAutor());
            livro.setIsbn(livroAtualizado.getIsbn());
            livro.setAnoPublicacao(livroAtualizado.getAnoPublicacao());
            livro.setGenero(livroAtualizado.getGenero());
            livro.setDescricao(livroAtualizado.getDescricao());
            livro.setAvaliacao(livroAtualizado.getAvaliacao());
            livro.setLido(livroAtualizado.getLido());
            return repository.save(livro);
        }).orElseThrow(() -> new RuntimeException("Livro não encontrado com id: " + id));
    }

    public void deletar(String id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Livro não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }
}