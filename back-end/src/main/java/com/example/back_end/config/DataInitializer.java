package com.example.back_end.config;

import com.example.back_end.entity.EquipamentoEntity;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.enums.UserRole;
import com.example.back_end.repository.EquipamentoRepository;
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
    private EquipamentoRepository equipamentoRepository;

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
        criarUsuarioSeNaoExiste("Ícaro", "IcaroTeste@gmail.com", "12345678", UserRole.TECNICO_N3);

        criarEquipamentoSeNaoExiste("Desktop Dell OptiPlex 7010", "PAT-0001", "Recepção");
        criarEquipamentoSeNaoExiste("Desktop Positivo Master D270", "PAT-0002", "TI");
        criarEquipamentoSeNaoExiste("Notebook Apple MacBook Pro M3", "PAT-0003", "Design");
        criarEquipamentoSeNaoExiste("Notebook Dell Latitude 5440", "PAT-0004", "Financeiro");
        criarEquipamentoSeNaoExiste("Notebook Inspiron 14 2 em 1", "PAT-0005", "Inventário");
        criarEquipamentoSeNaoExiste("Notebook Lenovo ThinkPad X1 Carbon", "PAT-0006", "Diretoria");
        criarEquipamentoSeNaoExiste("iPhone 15 Pro Max (corporativo)", "PAT-0007", "Marketing");
        criarEquipamentoSeNaoExiste("iPhone 17 Pro (corporativo)", "PAT-0008", "Consultor de Vendas");
    }

    private void criarUsuarioSeNaoExiste(String nome, String email, String senhaBruta, UserRole role) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(senhaBruta));
            usuario.setRole(role);

            usuarioRepository.save(usuario);
            System.out.println("✅ Usuário inicial criado com sucesso: " + email + " (" + role + ")");
        }
    }

    private void criarEquipamentoSeNaoExiste(String nome, String codigoPatrimonio, String setor) {
        if (equipamentoRepository.findByCodigoPatrimonio(codigoPatrimonio).isEmpty()) {
            EquipamentoEntity equipamento = new EquipamentoEntity();
            equipamento.setNome(nome);
            equipamento.setCodigoPatrimonio(codigoPatrimonio);
            equipamento.setSetor(setor);

            equipamentoRepository.save(equipamento);
            System.out.println("✅ Equipamento inicial criado com sucesso: " + nome + " (" + codigoPatrimonio + ")");
        }
    }
}