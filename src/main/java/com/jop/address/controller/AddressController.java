package com.jop.address.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jop.address.domain.Address;
import com.jop.address.dto.AddressDTO;
import com.jop.address.form.AddressForm;
import com.jop.address.service.AddressService;

/**
 * Services related to Address.
 * 
 * @author julianopontes
 *
 */
@RestController
@RequestMapping("address")
public class AddressController {

	@Autowired
	private AddressService service;

	/**
	 * Find Address by CEP.
	 * 
	 * @param cep
	 * @return list of found Addresses.
	 */
	@RequestMapping(method = GET, params = "cep")
	public List<AddressDTO> findByCEP(@RequestParam("cep") String cep) {
		service.validateCEPFormat(cep);

		List<Address> result = service.findAddresses(cep);

		return result.stream().map(a -> new AddressDTO(a)).collect(Collectors.toList());
	}

	/**
	 * Address creation.
	 * 
	 * @param form
	 * @return created Address.
	 */
	@RequestMapping(method = POST)
	@ResponseStatus(HttpStatus.CREATED)
	public AddressDTO create(@Valid @RequestBody AddressForm form) {
		service.validateCEPFormat(form.getCep());

		Address address = form.toAddress();

		address = service.create(address);

		return new AddressDTO(address);
	}

	/**
	 * Address read.
	 * 
	 * @param id
	 * @return Address.
	 */
	@RequestMapping(value = "{id}", method = GET)
	public AddressDTO read(@PathVariable("id") Long id) {
		return new AddressDTO(service.get(id));
	}

	/**
	 * Address update.
	 * 
	 * @param form
	 * @return updated Address.
	 */
	@RequestMapping(value = "{id}", method = PUT)
	public AddressDTO update(@PathVariable("id") Long id, @Valid @RequestBody AddressForm form) {
		service.validateCEPFormat(form.getCep());

		Address address = service.update(id, form.toAddress());

		return new AddressDTO(address);
	}

	/**
	 * Address deletion.
	 * 
	 * @param id
	 * @return deleted Address.
	 */
	@RequestMapping(value = "{id}", method = DELETE)
	public AddressDTO delete(@PathVariable("id") Long id) {
		Address address = service.delete(id);

		return new AddressDTO(address);
	}
}