package com.decagon.decapay.service.email;

import com.decagon.decapay.config.email.EmailConfig;
import com.decagon.decapay.payloads.request.email.Email;

public interface EmailSender {
  
  void send(Email email);

  void setEmailConfig(EmailConfig emailConfig);

}
