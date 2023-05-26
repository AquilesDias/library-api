package com.github.aquilesdias.libraryapi.service;

import com.github.aquilesdias.libraryapi.model.entity.Loan;
import com.github.aquilesdias.libraryapi.service.impl.LoanServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    @Value("${application.mail.lateloan.message}")
    private String messageLateLoan;
    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    private final LoanServiceImpl loanService;
    private final EmailService emailService;

    @Scheduled( cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoan();
        List<String> listMails = allLateLoans
                .stream()
                .map(loan -> loan.getCustomerEmail()).collect(Collectors.toList());

        emailService.sendMails( messageLateLoan, listMails);
    }
}
