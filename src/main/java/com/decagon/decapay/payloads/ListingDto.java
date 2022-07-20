package com.decagon.decapay.payloads;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ListingDto {
    protected Long id;
    protected String name;
    public ListingDto(Long id, String name) {
        this.id = id;
        this.name=name;
    }
}
