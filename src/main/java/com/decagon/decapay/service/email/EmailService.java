package com.decagon.decapay.service.email;

import com.decagon.decapay.payloads.Email;

public interface EmailService {
    void sendAsyncEmail(Email email);
}
