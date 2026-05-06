package com.senac.pi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmail(String destinatario, String assunto, String corpo) {
        log.info("### EMAIL ### Preparando envio para: {}", destinatario);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatario);
            message.setSubject(assunto);
            message.setText(corpo);
            
            log.info("### EMAIL ### Assunto: {}", assunto);
            
            mailSender.send(message);
            
            log.info("### EMAIL ### Sucesso: E-mail enviado com sucesso para {}!", destinatario);
            
        } catch (Exception e) {
            // Log de erro crítico - importante para debugar problemas com SMTP (Gmail, Outlook, etc)
            log.error("### EMAIL ### Falha ao enviar e-mail para {}. Detalhes: {}", destinatario, e.getMessage());
            
            // Opcional: Você pode relançar a exceção se quiser que o Spring trate
            // throw new RuntimeException("Erro ao disparar e-mail: " + e.getMessage());
        }
    }
}