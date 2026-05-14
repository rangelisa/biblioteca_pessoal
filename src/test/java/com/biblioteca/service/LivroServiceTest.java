package com.biblioteca.service;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository repository;

    @InjectMocks
    private LivroService service;

    private Livro livroPadrao;

    @BeforeEach
    void setUp() {
        livroPadrao = new Livro();
        livroPadrao.setId("1");
        livroPadrao.setTitulo("Clean Code");
        livroPadrao.setAutor("Robert C. Martin");
        livroPadrao.setIsbn("978-0132350884");
        livroPadrao.setAnoPublicacao(2008);
        livroPadrao.setGenero("Tecnologia");
        livroPadrao.setLido(false);
    }

    @Test
    void deveSalvarLivroComSucesso() {
        when(repository.findByIsbn(livroPadrao.getIsbn())).thenReturn(Optional.empty());
        when(repository.save(any(Livro.class))).thenAnswer(i -> i.getArguments()[0]);

        Livro salvo = service.salvar(livroPadrao);

        assertNotNull(salvo);
        assertEquals(livroPadrao.getIsbn(), salvo.getIsbn());
        verify(repository).save(livroPadrao);
    }

    @Test
    void naoDeveSalvarLivroComIsbnJaExistente() {
        when(repository.findByIsbn(livroPadrao.getIsbn())).thenReturn(Optional.of(livroPadrao));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.salvar(livroPadrao);
        });

        assertTrue(exception.getMessage().contains("Já existe um livro com este ISBN"));
        verify(repository, never()).save(any(Livro.class));
    }

    @Test
    void deveListarTodosOsLivros() {
        when(repository.findAll()).thenReturn(List.of(livroPadrao));

        List<Livro> livros = service.listarTodos();

        assertFalse(livros.isEmpty());
        assertEquals(1, livros.size());
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        when(repository.findById("1")).thenReturn(Optional.of(livroPadrao));

        Optional<Livro> retornado = service.buscarPorId("1");

        assertTrue(retornado.isPresent());
        assertEquals("Clean Code", retornado.get().getTitulo());
    }

    @Test
    void deveBuscarPorIsbn() {
        when(repository.findByIsbn(livroPadrao.getIsbn())).thenReturn(Optional.of(livroPadrao));

        Optional<Livro> retornado = service.buscarPorIsbn(livroPadrao.getIsbn());

        assertTrue(retornado.isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Clean", "Code", "Clean Code"})
    void deveBuscarPorTitulo(String titulo) {
        when(repository.findByTituloContainingIgnoreCase(titulo)).thenReturn(List.of(livroPadrao));

        List<Livro> livros = service.buscarPorTitulo(titulo);

        assertFalse(livros.isEmpty());
    }

    @Test
    void deveAtualizarLivroComSucesso() {
        when(repository.findById("1")).thenReturn(Optional.of(livroPadrao));
        when(repository.save(any(Livro.class))).thenAnswer(i -> i.getArguments()[0]);

        Livro novosDados = new Livro();
        novosDados.setTitulo("Clean Architecture");
        novosDados.setAutor("Robert C. Martin");
        novosDados.setIsbn("111-222");

        Livro atualizado = service.atualizar("1", novosDados);

        assertNotNull(atualizado);
        assertEquals("Clean Architecture", atualizado.getTitulo());
        assertEquals("111-222", atualizado.getIsbn());
    }

    @Test
    void deveLancarExcecaoAoAtualizarLivroInexistente() {
        when(repository.findById("2")).thenReturn(Optional.empty());

        Livro novosDados = new Livro();

        assertThrows(RuntimeException.class, () -> {
            service.atualizar("2", novosDados);
        });
    }

    @Test
    void deveDeletarLivroComSucesso() {
        when(repository.existsById("1")).thenReturn(true);
        doNothing().when(repository).deleteById("1");

        assertDoesNotThrow(() -> service.deletar("1"));
        verify(repository).deleteById("1");
    }

    @Test
    void deveLancarExcecaoAoDeletarLivroInexistente() {
        when(repository.existsById("2")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            service.deletar("2");
        });
        verify(repository, never()).deleteById(anyString());
    }
}
