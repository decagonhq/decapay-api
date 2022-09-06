package com.decagon.decapay.populator;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.model.user.User;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Service
public class UserSettingPopulator extends AbstractDataPopulator<UserDTO, UserSettings> {

    @Override
    public UserSettings populate(UserDTO source, UserSettings target) {

        target.setCountryCode(source.getCountryCode());
        target.setCurrencyCode(source.getCurrencyCode());
        target.setLanguage(source.getLanguageCode());

        return target;
    }


    @Override
    protected UserSettings createTarget() {
        return null;
    }
}
