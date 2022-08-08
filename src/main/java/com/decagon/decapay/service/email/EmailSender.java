package com.decagon.decapay.service.email;

import com.decagon.decapay.config.email.EmailConfig;

public interface EmailSender {
  
  void send(Email email);

  void setEmailConfig(EmailConfig emailConfig);

}
