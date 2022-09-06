package com.decagon.decapay.model.reference.currency;


import com.decagon.decapay.constants.SchemaConstants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(name = SchemaConstants.TABLE_CURRENCY)
@Cacheable
public class Currency  {
	private static final long serialVersionUID = -999926410367685145L;

	@Id
	@Column(name = "CURRENCY_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "CURRENCY_CURRENCY_CODE" ,nullable = false, unique = true)
	private java.util.Currency currency;
	
	@Column(name = "CURRENCY_SUPPORTED")
	private Boolean supported = true;
	
	@Column(name = "CURRENCY_CODE", unique = true)
	private String code;
	
	@Column(name = "CURRENCY_NAME", unique = true)
	private String name;
	
	public Currency() {
	}

	public java.util.Currency getCurrency() {
		return currency;
	}

	public void setCurrency(java.util.Currency currency) {
		this.currency = currency;
		this.code = currency.getCurrencyCode();
	}


	public String getCode() {
		if (currency.getCurrencyCode() != code) {
			return currency.getCurrencyCode();
		}
		return code;
	}
	
	public String getSymbol() {
		return currency.getSymbol();
	}

}
