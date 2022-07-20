package com.decagon.decapay.events;

import com.decagon.decapay.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@Builder
public class SuccessfulRegEvent extends ApplicationEvent {
    private User user;

    public SuccessfulRegEvent(User user) {
        super(user);
        this.user = user;
    }
}
