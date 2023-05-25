package com.github.aquilesdias.libraryapi.service.impl;

import com.github.aquilesdias.libraryapi.service.EmailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendMails(String messageLateLoan, List<String> listEmails) {

    }
}
