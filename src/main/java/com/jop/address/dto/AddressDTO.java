package com.jop.address.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringUtils;

import com.jop.address.domain.Address;
import com.jop.address.domain.State;

/**
 * DTO for Address.
 * 
 * @author julianopontes
 *
 */
@Data
@NoArgsConstructor
public class AddressDTO implements Serializable {

	private static final long serialVersionUID = -5106022614058154164L;

	private String street;
	private String cep;
	private String city;
	private Long id;
	private String neighborhood;
	private State state;
	private String number;
	private String complement;

	public AddressDTO(Address address) {
		this.street = address.getStreet();
		this.cep = new StringBuilder(StringUtils.leftPad(address.getCep().toString(), 8, "0")).insert(5, "-").toString();
		this.city = address.getCity();
		this.id = address.getId();
		this.neighborhood = address.getNeighborhood();
		this.state = address.getState();
		this.number = address.getNumber();
		this.complement = address.getComplement();
	}
}