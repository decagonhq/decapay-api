package com.decagon.decapay.populator;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.model.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Setter
@NoArgsConstructor
@Service
@Slf4j
public class UserAccountPopulator extends AbstractDataPopulator<UserDTO, User> {
    private  PasswordEncoder passwordEncoder;
    private ObjectMapper objectMapper;

    public UserAccountPopulator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User populate(UserDTO source, User target) {

        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setEmail(source.getEmail());
        target.setPassword(passwordEncoder.encode(source.getPassword()));

        UserSettingPopulator userSettingPopulator = new UserSettingPopulator();
        UserSettings settings = new UserSettings();
        userSettingPopulator.populate(source, settings);
        try {
            target.setUserSetting(objectMapper.writeValueAsString(settings));
        } catch (JsonProcessingException e) {
            log.error("error converting data",e);
            target.setUserSetting("");
        }
        return target;
    }

    @Override
    protected User createTarget() {
        return null;
    }
}
