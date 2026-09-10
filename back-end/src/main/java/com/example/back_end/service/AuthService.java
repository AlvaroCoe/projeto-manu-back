package com.example.back_end.service;

import com.example.back_end.dto.ForgotPasswordRequestDTO;
import com.example.back_end.dto.LoginRequestDTO;
import com.example.back_end.dto.LoginResponseDTO;
import com.example.back_end.dto.MensagemDTO;
import com.example.back_end.dto.ResetPasswordRequestDTO;
import com.example.back_end.entity.PasswordResetTokenEntity;
import com.example.back_end.entity.UsuarioEntity;
import com.example.back_end.exception.ResourceNotFoundException;
import com.example.back_end.repository.PasswordResetTokenRepository;
import com.example.back_end.repository.UsuarioRepository;
import com.example.back_end.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.password-reset.expiration-minutes:30}")
    private long expiracaoMinutos;

    @Value("${app.password-reset.cooldown-seconds:60}")
    private long cooldownSegundos;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.mail.from}")
    private String remetente;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new ResourceNotFoundException("E-mail ou senha inválidos");
        }

        // getAtivo() != null evita quebrar contas antigas que ainda não tinham essa coluna
        if (usuario.getAtivo() != null && !usuario.getAtivo()) {
            throw new IllegalStateException("Esta conta foi desativada. Fale com um administrador.");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRole().name());

        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                token
        );
    }

    // Solicita a recuperação de senha. Válido para SOLICITANTE, N1, N2, N3 e ADMIN,
    // pois usa o mesmo cadastro (tb_usuarios) para todos os perfis.
    public MensagemDTO forgotPassword(ForgotPasswordRequestDTO dto) {
        usuarioRepository.findByEmail(dto.email()).ifPresent(usuario -> {

            List<PasswordResetTokenEntity> tokensAtivos =
                    passwordResetTokenRepository.findByUsuarioIdAndUsadoFalse(usuario.getId());

            // Evita reenvio em massa: se já foi gerado um token há pouco tempo, não gera outro
            boolean solicitacaoRecente = tokensAtivos.stream()
                    .anyMatch(t -> t.getDataCriacao().isAfter(LocalDateTime.now().minusSeconds(cooldownSegundos)));

            if (!solicitacaoRecente) {
                // Invalida links antigos: só o token mais recente pode ser usado
                passwordResetTokenRepository.deleteAll(tokensAtivos);

                PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
                tokenEntity.setUsuario(usuario);
                tokenEntity.setToken(UUID.randomUUID().toString());
                tokenEntity.setDataCriacao(LocalDateTime.now());
                tokenEntity.setDataExpiracao(LocalDateTime.now().plusMinutes(expiracaoMinutos));
                tokenEntity.setUsado(false);
                passwordResetTokenRepository.save(tokenEntity);

                enviarEmailRecuperacao(usuario, tokenEntity.getToken());
            }
        });

        // Mensagem sempre igual, exista ou não o e-mail na base,
        // para não revelar quais e-mails estão cadastrados no sistema
        return new MensagemDTO("Se o e-mail informado estiver cadastrado, você receberá um link para redefinir sua senha.");
    }

    // Concretiza a troca de senha a partir do token recebido por e-mail
    public MensagemDTO resetPassword(ResetPasswordRequestDTO dto) {
        PasswordResetTokenEntity tokenEntity = passwordResetTokenRepository.findByToken(dto.token())
                .orElseThrow(() -> new IllegalStateException("Link de redefinição inválido ou expirado."));

        if (Boolean.TRUE.equals(tokenEntity.getUsado())
                || tokenEntity.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Link de redefinição inválido ou expirado.");
        }

        UsuarioEntity usuario = tokenEntity.getUsuario();
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);

        // Token de uso único: não pode ser reaproveitado depois de trocar a senha
        tokenEntity.setUsado(true);
        passwordResetTokenRepository.save(tokenEntity);

        return new MensagemDTO("Senha redefinida com sucesso. Você já pode fazer login com a nova senha.");
    }

    private void enviarEmailRecuperacao(UsuarioEntity usuario, String token) {
        String link = frontendUrl + "/redefinir-senha?token=" + token;

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(usuario.getEmail());
        mensagem.setSubject("Recuperação de senha - Help Desk TI");
        mensagem.setText(
                "Olá, " + usuario.getNome() + "!\n\n" +
                        "Recebemos uma solicitação para redefinir sua senha.\n" +
                        "Clique no link abaixo para criar uma nova senha (válido por " + expiracaoMinutos + " minutos):\n\n" +
                        link + "\n\n" +
                        "Se você não solicitou essa alteração, apenas ignore este e-mail."
        );

        try {
            mailSender.send(mensagem);
        } catch (MailException e) {
            // Não deixa a falha de envio derrubar a requisição nem vazar detalhes ao cliente;
            // fica registrado no log do servidor para diagnóstico
            System.err.println("⚠️ Falha ao enviar e-mail de recuperação de senha para " + usuario.getEmail() + ": " + e.getMessage());
        }
    }
}