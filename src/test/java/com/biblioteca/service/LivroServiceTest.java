package com.biblioteca.service;

import com.biblioteca.config.MongoTestConfig;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(MongoTestConfig.class)
class LivroServiceTest {

    @Autowired
    private LivroRepository repository;

    @Autowired
    private LivroService service;

    private Livro livroPadrao;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        livroPadrao = new Livro();
        livroPadrao.setTitulo("Clean Code");
        livroPadrao.setAutor("Robert C. Martin");
        livroPadrao.setIsbn("978-0132350884");
        livroPadrao.setAnoPublicacao(2008);
        livroPadrao.setGenero("Tecnologia");
        livroPadrao.setLido(false);
    }

    @Test
    void deveSalvarLivroComSucesso() {
        Livro salvo = service.salvar(livroPadrao);

        assertNotNull(salvo.getId());
        assertEquals(livroPadrao.getIsbn(), salvo.getIsbn());
        
        Optional<Livro> noBanco = repository.findById(salvo.getId());
        assertTrue(noBanco.isPresent());
    }

    @Test
    void naoDeveSalvarLivroComIsbnJaExistente() {
        // Salva o primeiro livro
        service.salvar(livroPadrao);

        // Tenta salvar o segundo com o mesmo ISBN
        Livro segundo = new Livro();
        segundo.setTitulo("Outro Livro");
        segundo.setAutor("Outro Autor");
        segundo.setIsbn("978-0132350884"); // Mesma String

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.salvar(segundo);
        });

        assertTrue(exception.getMessage().contains("Já existe um livro com este ISBN"));
        assertEquals(1, repository.findAll().size()); // Apenas 1 no banco
    }

    @Test
    void deveListarTodosOsLivros() {
        service.salvar(livroPadrao);

        List<Livro> livros = service.listarTodos();

        assertFalse(livros.isEmpty());
        assertEquals(1, livros.size());
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        Livro salvo = service.salvar(livroPadrao);

        Optional<Livro> retornado = service.buscarPorId(salvo.getId());

        assertTrue(retornado.isPresent());
        assertEquals("Clean Code", retornado.get().getTitulo());
    }

    @Test
    void deveBuscarPorIsbn() {
        service.salvar(livroPadrao);

        Optional<Livro> retornado = service.buscarPorIsbn("978-0132350884");

        assertTrue(retornado.isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Clean", "Code", "Clean Code", "clean"})
    void deveBuscarPorTitulo(String titulo) {
        service.salvar(livroPadrao);

        List<Livro> livros = service.buscarPorTitulo(titulo);

        assertFalse(livros.isEmpty());
    }

    @Test
    void deveAtualizarLivroComSucesso() {
        Livro salvo = service.salvar(livroPadrao);

        Livro novosDados = new Livro();
        novosDados.setTitulo("Clean Architecture");
        novosDados.setAutor("Robert C. Martin");
        novosDados.setIsbn("111-222");

        Livro atualizado = service.atualizar(salvo.getId(), novosDados);

        assertNotNull(atualizado);
        assertEquals("Clean Architecture", atualizado.getTitulo());
        assertEquals("111-222", atualizado.getIsbn());
    }

    @Test
    void deveLancarExcecaoAoAtualizarLivroInexistente() {
        Livro novosDados = new Livro();

        assertThrows(RuntimeException.class, () -> {
            service.atualizar("id-invalido", novosDados);
        });
    }

    @Test
    void deveDeletarLivroComSucesso() {
        Livro salvo = service.salvar(livroPadrao);

        assertDoesNotThrow(() -> service.deletar(salvo.getId()));
        
        Optional<Livro> deletado = repository.findById(salvo.getId());
        assertFalse(deletado.isPresent());
    }

    @Test
    void deveLancarExcecaoAoDeletarLivroInexistente() {
        assertThrows(RuntimeException.class, () -> {
            service.deletar("id-invalido");
        });
    }
}
