package com.github.aquilesdias.libraryapi.service.impl;

import com.github.aquilesdias.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${ application.mail.default-remetent }")
    private String remetent;

    @Override
    public void sendMails(String messageLateLoan, List<String> listEmails) {

        String [] emails = listEmails.toArray(new String[ listEmails.size()]);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(remetent);
        simpleMailMessage.setSubject("Livro atrasado.");
        simpleMailMessage.setText(messageLateLoan);
        simpleMailMessage.setTo(emails);
    }
}
