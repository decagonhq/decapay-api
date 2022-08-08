package com.decagon.decapay.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
public class SearchCriteria {
    private String key;
    private Object value;

    public SearchCriteria(String searchKey, Object searchValue) {
            this.key=searchKey;
            this.value=searchValue;
    }
}
