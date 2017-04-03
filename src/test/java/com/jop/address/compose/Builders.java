package com.jop.address.compose;

import static com.jop.address.utils.CEPUtil.isCEP;

import org.springframework.util.Assert;

import com.jop.address.domain.Address;
import com.jop.address.domain.State;

/**
 * Auxiliary class for building objects.
 * 
 * @author julianopontes
 *
 */
public class Builders {

	public static Address.AddressBuilder address(String cep, String street, String number, String city, State state) {
		Assert.hasText(cep, "Inform CEP");
		isCEP(cep);
		Assert.hasText(street, "Inform street");
		Assert.hasText(city, "Inform city");
		Assert.notNull(state, "Inform state");
		Assert.notNull(number, "Inform number");

		return Address.builder().cep(Integer.valueOf(cep.replace("-", ""))).street(street).number(number).city(city)
		    .state(state);
	}
}