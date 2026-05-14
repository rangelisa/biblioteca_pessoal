package com.biblioteca.controller;

import com.biblioteca.config.MongoTestConfig;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MongoTestConfig.class)
class LivroControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LivroRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    private Livro criarLivro(String titulo, String autor, String isbn) {
        Livro livro = new Livro();
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setIsbn(isbn);
        livro.setAnoPublicacao(2020);
        livro.setGenero("Ficção");
        livro.setLido(false);
        return livro;
    }

    @Test
    void deveCriarLivroComSucesso() {
        Livro livro = criarLivro("Dom Casmurro", "Machado de Assis", "978-0-00-001");

        ResponseEntity<Livro> response = restTemplate.postForEntity("/api/livros", livro, Livro.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dom Casmurro", response.getBody().getTitulo());
    }

    @Test
    void deveRetornarConflictParaIsbnDuplicado() {
        Livro livro = criarLivro("Dom Casmurro", "Machado de Assis", "978-0-00-001");
        repository.save(livro);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/livros", livro, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void deveListarTodosOsLivros() {
        repository.save(criarLivro("Livro A", "Autor A", "111"));
        repository.save(criarLivro("Livro B", "Autor B", "222"));

        ResponseEntity<List<Livro>> response = restTemplate.exchange(
                "/api/livros",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Livro>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void deveBuscarLivroPorId() {
        Livro salvo = repository.save(criarLivro("O Alquimista", "Paulo Coelho", "333"));

        ResponseEntity<Livro> response = restTemplate.getForEntity("/api/livros/" + salvo.getId(), Livro.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("O Alquimista", response.getBody().getTitulo());
    }

    @Test
    void deveRetornarNotFoundParaIdInexistente() {
        ResponseEntity<Livro> response = restTemplate.getForEntity("/api/livros/id-inexistente", Livro.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deveAtualizarLivro() {
        Livro salvo = repository.save(criarLivro("Título Antigo", "Autor", "444"));
        salvo.setTitulo("Título Novo");

        HttpEntity<Livro> requestUpdate = new HttpEntity<>(salvo);
        ResponseEntity<Livro> response = restTemplate.exchange("/api/livros/" + salvo.getId(), HttpMethod.PUT, requestUpdate, Livro.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Título Novo", response.getBody().getTitulo());
    }

    @Test
    void deveDeletarLivro() {
        Livro salvo = repository.save(criarLivro("Para Deletar", "Autor", "555"));

        ResponseEntity<Void> response = restTemplate.exchange("/api/livros/" + salvo.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deveBuscarPorTitulo() {
        repository.save(criarLivro("Clean Code", "Robert Martin", "666"));

        ResponseEntity<List<Livro>> response = restTemplate.exchange(
                "/api/livros/buscar?titulo=Clean",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Livro>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Clean Code", response.getBody().get(0).getTitulo());
    }

    @Test
    void deveBuscarPorAutor() {
        repository.save(criarLivro("Clean Code", "Robert Martin", "777"));

        ResponseEntity<List<Livro>> response = restTemplate.exchange(
                "/api/livros/buscar?autor=Robert",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Livro>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Robert Martin", response.getBody().get(0).getAutor());
    }
}