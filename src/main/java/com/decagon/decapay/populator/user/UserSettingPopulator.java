package com.decagon.decapay.populator.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.user.SignUpRequestDTO;
import com.decagon.decapay.populator.AbstractDataPopulator;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Service
public class UserSettingPopulator extends AbstractDataPopulator<SignUpRequestDTO, UserSettings> {

    @Override
    public UserSettings populate(SignUpRequestDTO source, UserSettings target) {

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
