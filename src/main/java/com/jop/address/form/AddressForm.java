package com.jop.address.form;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringUtils;

import com.jop.address.domain.Address;
import com.jop.address.domain.State;

/**
 * Form for Address creation.
 * 
 * @author julianopontes
 *
 */
@Data
@NoArgsConstructor
public class AddressForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4341355295339135070L;

	@NotNull(message = "street is required")
	private String street;

	@NotNull(message = "cep is required")
	private String cep;

	@NotNull(message = "city is required")
	private String city;

	@NotNull(message = "state is required")
	private State state;

	@NotNull(message = "number is required")
	private String number;

	private String neighborhood;
	private String complement;

	public AddressForm(Address address) {
		this.street = address.getStreet();
		this.cep = new StringBuilder(StringUtils.leftPad(address.getCep().toString(), 8, "0")).insert(5, "-").toString();
		this.city = address.getCity();
		this.neighborhood = address.getNeighborhood();
		this.state = address.getState();
		this.number = address.getNumber();
		this.complement = address.getComplement();
	}

	public Address toAddress() {
		return Address.builder().cep(Integer.valueOf(cep == null ? "0" : cep.replace("-", ""))).city(city)
		    .complement(complement).neighborhood(neighborhood).number(number).state(state).street(street).build();
	}
}