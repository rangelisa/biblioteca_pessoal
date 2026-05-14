package com.biblioteca.controller;

import com.biblioteca.config.MongoTestConfig;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MongoTestConfig.class)
class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    private Usuario criarUsuario(String nome, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        return usuario;
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        Usuario usuario = criarUsuario("Maria", "maria@teste.com", "senha123");

        ResponseEntity<Usuario> response = restTemplate.postForEntity("/api/usuarios/cadastrar", usuario, Usuario.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria", response.getBody().getNome());
        assertEquals("maria@teste.com", response.getBody().getEmail());
        assertNull(response.getBody().getSenha()); // A senha nunca deve retornar
    }

    @Test
    void deveRetornarConflictAoCadastrarEmailExistente() {
        Usuario usuario = criarUsuario("João", "joao@teste.com", "senha321");
        // Cadastra o primeiro
        restTemplate.postForEntity("/api/usuarios/cadastrar", usuario, Usuario.class);

        // Tenta cadastrar novamente com o mesmo e-mail
        Usuario usuarioNovo = criarUsuario("João Novo", "joao@teste.com", "outrasenha");
        ResponseEntity<String> response = restTemplate.postForEntity("/api/usuarios/cadastrar", usuarioNovo, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("Email já cadastrado"));
    }
}
