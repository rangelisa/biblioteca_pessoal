package com.biblioteca.service;

import com.biblioteca.config.MongoTestConfig;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(MongoTestConfig.class)
class UsuarioServiceTest {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioService service;

    private Usuario usuarioPadrao;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        usuarioPadrao = new Usuario();
        usuarioPadrao.setNome("Teste");
        usuarioPadrao.setEmail("teste@teste.com");
        usuarioPadrao.setSenha("123456");
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        Usuario salvo = service.cadastrar(usuarioPadrao);

        assertNotNull(salvo.getId());
        assertNotEquals("123456", salvo.getSenha()); // A senha deve estar codificada
        
        List<Usuario> usuarios = repository.findAll();
        assertEquals(1, usuarios.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"existente@teste.com", "admin@teste.com", "user@teste.com"})
    void naoDeveCadastrarUsuarioComEmailJaExistente(String email) {
        // Cadastra primeiro usuário
        Usuario primeiro = new Usuario();
        primeiro.setNome("Primeiro");
        primeiro.setEmail(email);
        primeiro.setSenha("1234");
        service.cadastrar(primeiro);

        // Tenta cadastrar outro com mesmo e-mail
        Usuario segundo = new Usuario();
        segundo.setNome("Segundo");
        segundo.setEmail(email);
        segundo.setSenha("5678");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrar(segundo);
        });

        assertEquals("Email já cadastrado.", exception.getMessage());
        assertEquals(1, repository.findAll().size()); // Só pode ter salvo o primeiro
    }

    @Test
    void deveListarTodosOsUsuarios() {
        service.cadastrar(usuarioPadrao);

        List<Usuario> lista = service.listarTodos();

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }

    @Test
    void deveCarregarUsuarioPorNomeDeUsuarioComSucesso() {
        service.cadastrar(usuarioPadrao);

        UserDetails userDetails = service.loadUserByUsername(usuarioPadrao.getEmail());

        assertNotNull(userDetails);
        assertEquals(usuarioPadrao.getEmail(), userDetails.getUsername());
    }

    @Test
    void deveLancarExcecaoAoCarregarUsuarioInexistente() {
        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("naoexiste@teste.com");
        });
    }
}
