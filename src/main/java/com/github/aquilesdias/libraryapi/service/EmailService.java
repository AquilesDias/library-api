package com.github.aquilesdias.libraryapi.service;

import java.util.List;

public interface EmailService {
    void sendMails(String messageLateLoan, List<String> listEmails);
}
