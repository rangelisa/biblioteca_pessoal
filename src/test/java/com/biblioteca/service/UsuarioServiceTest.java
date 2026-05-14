package com.biblioteca.service;

import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuarioPadrao;

    @BeforeEach
    void setUp() {
        usuarioPadrao = new Usuario();
        usuarioPadrao.setId("1");
        usuarioPadrao.setNome("Teste");
        usuarioPadrao.setEmail("teste@teste.com");
        usuarioPadrao.setSenha("123456");
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        when(repository.existsByEmail(usuarioPadrao.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("senhaCodificada");
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArguments()[0]);

        Usuario salvo = service.cadastrar(usuarioPadrao);

        assertNotNull(salvo);
        assertEquals("senhaCodificada", salvo.getSenha());
        verify(repository).save(usuarioPadrao);
    }

    @ParameterizedTest
    @ValueSource(strings = {"existente@teste.com", "admin@teste.com", "user@teste.com"})
    void naoDeveCadastrarUsuarioComEmailJaExistente(String email) {
        usuarioPadrao.setEmail(email);
        when(repository.existsByEmail(email)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrar(usuarioPadrao);
        });

        assertEquals("Email já cadastrado.", exception.getMessage());
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void deveListarTodosOsUsuarios() {
        when(repository.findAll()).thenReturn(List.of(usuarioPadrao));

        List<Usuario> lista = service.listarTodos();

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }

    @Test
    void deveCarregarUsuarioPorNomeDeUsuarioComSucesso() {
        when(repository.findByEmail(usuarioPadrao.getEmail())).thenReturn(Optional.of(usuarioPadrao));

        UserDetails userDetails = service.loadUserByUsername(usuarioPadrao.getEmail());

        assertNotNull(userDetails);
        assertEquals(usuarioPadrao.getEmail(), userDetails.getUsername());
        assertEquals(usuarioPadrao.getSenha(), userDetails.getPassword());
    }

    @Test
    void deveLancarExcecaoAoCarregarUsuarioInexistente() {
        when(repository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("naoexiste@teste.com");
        });
    }
}
