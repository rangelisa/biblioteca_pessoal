package com.biblioteca.controller;

import com.biblioteca.config.MongoTestConfig;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MongoTestConfig.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

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
    void deveCriarLivroComSucesso() throws Exception {
        Livro livro = criarLivro("Dom Casmurro", "Machado de Assis", "978-0-00-001");

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor").value("Machado de Assis"));
    }

    @Test
    void deveRetornarConflictParaIsbnDuplicado() throws Exception {
        Livro livro = criarLivro("Dom Casmurro", "Machado de Assis", "978-0-00-001");
        repository.save(livro);

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livro)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveListarTodosOsLivros() throws Exception {
        repository.save(criarLivro("Livro A", "Autor A", "111"));
        repository.save(criarLivro("Livro B", "Autor B", "222"));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deveBuscarLivroPorId() throws Exception {
        Livro salvo = repository.save(criarLivro("O Alquimista", "Paulo Coelho", "333"));

        mockMvc.perform(get("/api/livros/" + salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("O Alquimista"));
    }

    @Test
    void deveRetornarNotFoundParaIdInexistente() throws Exception {
        mockMvc.perform(get("/api/livros/id-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarLivro() throws Exception {
        Livro salvo = repository.save(criarLivro("Título Antigo", "Autor", "444"));
        salvo.setTitulo("Título Novo");

        mockMvc.perform(put("/api/livros/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salvo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Título Novo"));
    }

    @Test
    void deveDeletarLivro() throws Exception {
        Livro salvo = repository.save(criarLivro("Para Deletar", "Autor", "555"));

        mockMvc.perform(delete("/api/livros/" + salvo.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarPorTitulo() throws Exception {
        repository.save(criarLivro("Clean Code", "Robert Martin", "666"));

        mockMvc.perform(get("/api/livros/buscar?titulo=Clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Clean Code"));
    }

    @Test
    void deveBuscarPorAutor() throws Exception {
        repository.save(criarLivro("Clean Code", "Robert Martin", "777"));

        mockMvc.perform(get("/api/livros/buscar?autor=Robert"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].autor").value("Robert Martin"));
    }
}