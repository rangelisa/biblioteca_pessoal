package com.biblioteca.repository;

import com.biblioteca.model.Livro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends MongoRepository<Livro, String> {
    List<Livro> findByAutorContainingIgnoreCase(String autor);
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
    List<Livro> findByLido(Boolean lido);
    List<Livro> findByGeneroIgnoreCase(String genero);
    Optional<Livro> findByIsbn(String isbn);
}