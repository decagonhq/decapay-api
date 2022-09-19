package com.decagon.decapay.populator.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.user.SignUpRequestDTO;
import com.decagon.decapay.dto.user.UserDto;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.populator.AbstractDataPopulator;
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
public class UserPopulator extends AbstractDataPopulator<UserDto, User> {
    private  PasswordEncoder passwordEncoder;
    private ObjectMapper objectMapper;

    public UserPopulator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User populate(UserDto source, User target) {

        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setEmail(source.getEmail());
        if (target.getId()==null){
            target.setPassword(passwordEncoder.encode(((SignUpRequestDTO)source).getPassword()));

            UserSettingPopulator userSettingPopulator = new UserSettingPopulator();
            UserSettings settings = new UserSettings();
            userSettingPopulator.populate((SignUpRequestDTO) source, settings);
            try {
                target.setUserSetting(objectMapper.writeValueAsString(settings));
            } catch (JsonProcessingException e) {
                log.error("error converting data",e);
                target.setUserSetting("");
            }
        }
        return target;
    }

    @Override
    protected User createTarget() {
        return null;
    }
}
