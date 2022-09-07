package com.decagon.decapay.dto.reference;

import lombok.Data;

import java.util.Collection;

@Data
public class UserSettingReferencesDto {
    private Collection<ReferenceListingDto> countries;
    private Collection<ReferenceListingDto> languages;
    private Collection<ReferenceListingDto> currencies;
}
