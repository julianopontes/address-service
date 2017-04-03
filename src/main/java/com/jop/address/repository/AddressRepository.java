package com.jop.address.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jop.address.domain.Address;

/**
 * Repository for address.
 * 
 * @author julianopontes
 *
 */
public interface AddressRepository extends CrudRepository<Address, Long> {

	/**
	 * List address by cep.
	 * 
	 * @param cep
	 * @return Address list.
	 */
	List<Address> findByCep(Integer cep);

	/**
	 * Find address by cep and street.
	 * 
	 * @param cep
	 * @param street
	 * @return found address.
	 */
	int countByCepAndStreet(Integer cep, String street);
}