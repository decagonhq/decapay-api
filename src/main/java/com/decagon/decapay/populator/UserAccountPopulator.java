package com.decagon.decapay.populator;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.model.user.User;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Setter
@NoArgsConstructor
@Service
public class UserAccountPopulator extends AbstractDataPopulator<UserDTO, User> {
    private  PasswordEncoder passwordEncoder;
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
        target.setUserSetting(settings.toJSONString());
        return target;
    }

    @Override
    protected User createTarget() {
        return null;
    }
}
