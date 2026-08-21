package com.example.back_end.config;

import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.enums.UserRole;
import com.example.back_end.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        criarUsuarioSeNaoExiste("Alvaro", "AlvaroTeste@gmail.com", "12345678", UserRole.SOLICITANTE);
        criarUsuarioSeNaoExiste("Alvaro1", "AlvaroTeste1@gmail.com", "12345678", UserRole.TECNICO_N1);
        criarUsuarioSeNaoExiste("Alvaro2", "AlvaroTeste2@gmail.com", "12345678", UserRole.TECNICO_N2);
        criarUsuarioSeNaoExiste("Alvaro3", "AlvaroTeste3@gmail.com", "12345678", UserRole.TECNICO_N3);
        criarUsuarioSeNaoExiste("Danilo", "DaniloTeste@gmail.com", "12345678", UserRole.TECNICO_N3);
        criarUsuarioSeNaoExiste("Gabrielle", "GabrielleTeste@gmail.com", "12345678", UserRole.TECNICO_N2);
        criarUsuarioSeNaoExiste("Stephanie", "StephanieTeste@gmail.com", "12345678", UserRole.TECNICO_N1);
    }

    private void criarUsuarioSeNaoExiste(String nome, String email, String senhaBruta, UserRole role) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(senhaBruta)); // Senha criptografada
            usuario.setRole(role);

            usuarioRepository.save(usuario);
            System.out.println("✅ Usuário inicial criado com sucesso: " + email + " (" + role + ")");
        }
    }
}
