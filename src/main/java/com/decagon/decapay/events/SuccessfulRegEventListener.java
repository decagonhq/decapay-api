package com.decagon.decapay.events;

import com.decagon.decapay.utils.EmailTemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuccessfulRegEventListener implements ApplicationListener<SuccessfulRegEvent> {
    private final EmailTemplateUtil util;

    @Override
    public void onApplicationEvent(SuccessfulRegEvent event) {
        this.util.sendSuccessfulRegEmail(event.getUser());
    }
}
