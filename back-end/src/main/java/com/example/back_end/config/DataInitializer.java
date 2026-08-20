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
        if (usuarioRepository.findByEmail("AlvaroTeste@gmail.com").isEmpty()) {
            UsuarioEntity admin = new UsuarioEntity();
            admin.setNome("Alvaro Admin");
            admin.setEmail("AlvaroTeste@gmail.com");
            admin.setSenha(passwordEncoder.encode("12345678")); // Senha criptografada
            admin.setRole(UserRole.TECNICO_N1);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuário inicial criado com sucesso: AlvaroTeste@gmail.com");
        }
    }
}