package com.decagon.decapay.model.reference.country;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_COUNTRY;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = TABLE_COUNTRY)
public class Country{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=100)
    private String name;

    @Column(length=3)
    private String isoCode;

}
