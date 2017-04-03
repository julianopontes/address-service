package com.jop.address.service;

import static com.jop.address.utils.CEPUtil.isCEP;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jop.address.domain.Address;
import com.jop.address.exception.InvalidCEPException;
import com.jop.address.exception.NotFoundException;
import com.jop.address.repository.AddressRepository;

/**
 * Address services.
 * 
 * @author julianopontes
 *
 */
@Service
public class AddressService {

	@Autowired
	private AddressRepository addressRepository;

	/**
	 * Service for finding Addresses by CEP.
	 * 
	 * @param cep
	 * @return Address list
	 */
	public List<Address> findAddresses(String cep) {
		Integer cepNumber = Integer.valueOf(cep.replace("-", ""));

		List<Address> addresses = new ArrayList<Address>();
		boolean hasCep = false;
		while (!hasCep && cepNumber > 0) {
			addresses = addressRepository.findByCep(cepNumber);
			cepNumber = Integer.valueOf(String.format("%s0", cepNumber.toString().substring(1)));
			hasCep = !addresses.isEmpty();
		}

		return addresses;
	}

	/**
	 * Validate if current CEP format is correct.
	 * 
	 * @param cep
	 */
	public void validateCEPFormat(String cep) {
		if (!isCEP(cep)) {
			throw new InvalidCEPException(cep);
		}
	}

	/**
	 * Validation and creation of Address.
	 * 
	 * @param address
	 * @return created Address.
	 */
	public Address create(Address address) {
		if (addressRepository.countByCepAndStreet(address.getCep(), address.getStreet()) > 0) {
			throw new DuplicateKeyException("Address for informed cep and street already exists");
		}

		address = addressRepository.save(address);

		return address;
	}

	/**
	 * Validation and update of Address.
	 * 
	 * @param id
	 * @param address
	 * @return updated Address.
	 */
	@Transactional
	public Address update(Long id, Address address) {
		Address loaded = get(id);
		loaded.setCep(address.getCep());
		loaded.setCity(address.getCity());
		loaded.setComplement(address.getComplement());
		loaded.setNeighborhood(address.getNeighborhood());
		loaded.setNumber(address.getNumber());
		loaded.setState(address.getState());
		loaded.setStreet(address.getStreet());

		address = addressRepository.save(loaded);

		return address;
	}

	/**
	 * Get an Address.
	 * 
	 * @param id
	 * @return Address.
	 */
	public Address get(Long id) {
		Address address = addressRepository.findOne(id);

		if (address == null) {
			throw new NotFoundException(Address.class.getSimpleName());
		}

		return address;
	}

	/**
	 * Delete an Address.
	 * 
	 * @param id
	 * @return deleted Address.
	 */
	@Transactional
	public Address delete(Long id) {
		Address address = get(id);

		addressRepository.delete(address);

		return address;
	}
}