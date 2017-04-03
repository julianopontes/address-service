package com.jop.address.controller;

import static com.jop.address.compose.Builders.address;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonassert.JsonAssert;
import com.jop.address.ApplicationTest;
import com.jop.address.domain.Address;
import com.jop.address.domain.State;
import com.jop.address.form.AddressForm;
import com.jop.address.repository.AddressRepository;

/**
 * Address tests.
 * 
 * @author julianopontes
 *
 */
public class AddressControllerTest extends ApplicationTest {

	@Autowired
	private AddressRepository addressRepository;

	@Test
	public void testFindByValidCEPWithNumberFormat() throws JsonProcessingException, IOException {
		// Building addresses for querying.
		Address address = address("04325000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo",
		    State.SP).build();

		// Address persistence.
		saveall(address);

		// execute request and receive response converted to JSON.
		JsonNode json = get("address").queryParam("cep", address.getCep()).expectedStatus(HttpStatus.OK).getJson();

		// Assert verification
		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertThat("$", hasSize(1))
		    .assertEquals("$.[0].cep", "04325-000")
		    .assertEquals("$.[0].street", "Avenida Engenheiro Armando de Arruda Pereira")
		    .assertEquals("$.[0].number", "3411")
		    .assertEquals("$.[0].city", "São Paulo")
		    .assertEquals("$.[0].state", "SP");
	}

	@Test
	public void testFindByValidCEPWithStringFormat() throws JsonProcessingException, IOException {
		// Building addresses for querying with CEP in string format.
		Address address = address("04325-000", "Avenida Engenheiro Armando de Arruda Pereira", "3411",
		    "São Paulo", State.SP).build();

		// Address persistence.
		saveall(address);

		// execute request and receive response converted to JSON sending CEP with
		// string format.
		JsonNode json = get("address").queryParam("cep", "04325-000").expectedStatus(HttpStatus.OK).getJson();

		// Assert verification
		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertThat("$", hasSize(1))
		    .assertEquals("$.[0].cep", "04325-000")
		    .assertEquals("$.[0].street", "Avenida Engenheiro Armando de Arruda Pereira")
		    .assertEquals("$.[0].number", "3411")
		    .assertEquals("$.[0].city", "São Paulo")
		    .assertEquals("$.[0].state", "SP");
	}

	@Test
	public void testFindByInvalidCEP() throws JsonProcessingException, IOException {
		// execute request and receive response converted to JSON.
		JsonNode json = get("address").queryParam("cep", "AAA").expectedStatus(HttpStatus.PRECONDITION_FAILED).getJson();

		// Assert verification
		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertEquals("$.message", "Invalid CEP: AAA");
	}

	@Test
	public void testFindByValidCEPNoAddress() throws JsonProcessingException, IOException {
		// Building addresses for querying with CEP in string format.
		Address address = address("04325-000", "Avenida Engenheiro Armando de Arruda Pereira", "3411",
		    "São Paulo", State.SP).build();

		// Address persistence.
		saveall(address);

		// execute request and receive response converted to JSON sending CEP with
		// no Address existent.
		JsonNode json = get("address").queryParam("cep", "11104-325").expectedStatus(HttpStatus.OK).getJson();

		// Assert verification
		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertThat("$", hasSize(1))
		    .assertEquals("$.[0].cep", "04325-000")
		    .assertEquals("$.[0].street", "Avenida Engenheiro Armando de Arruda Pereira")
		    .assertEquals("$.[0].number", "3411")
		    .assertEquals("$.[0].city", "São Paulo")
		    .assertEquals("$.[0].state", "SP")
		    .assertNull("$.[0].neighborhood")
		    .assertNull("$.[0].complement");
	}

	@Test
	public void testFindByValidCEPWithoutResults() throws JsonProcessingException, IOException {
		// Building addresses for querying with CEP in string format.
		Address address = address("04325-000", "Avenida Engenheiro Armando de Arruda Pereira", "3411",
		    "São Paulo", State.SP).build();

		// Address persistence.
		saveall(address);

		// execute request and receive response converted to JSON sending CEP
		// without Address related.
		JsonNode json = get("address").queryParam("cep", "11111-325").expectedStatus(HttpStatus.OK).getJson();

		// Assert verification
		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertThat("$", hasSize(0));
	}

	@Test
	public void testFindByValidCEPWithMultipleAddress() throws JsonProcessingException, IOException {
		// Building addresses for querying with CEP in string format.
		Address address1 = address("04325-000", "Avenida Engenheiro Armando de Arruda Pereira", "3411",
		    "São Paulo", State.SP).build();
		Address address2 = address("04325-000", "Av Euclides", "3411", "São Paulo", State.SP).build();
		Address address3 = address("04326-080", "Av Euclides", "3411", "São Paulo", State.SP).build();

		// Addresses persistence.
		saveall(address1, address2, address3);

		// execute request and receive response converted to JSON sending CEP with
		// string format.
		JsonNode json = get("address").queryParam("cep", "04325-000").expectedStatus(HttpStatus.OK).getJson();

		// Assert verification
		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertThat("$", hasSize(2))
		    .assertThat("$.[*].cep", hasItems("04325-000", "04325-000"))
		    .assertThat("$.[*].street", hasItems("Avenida Engenheiro Armando de Arruda Pereira", "Av Euclides"));
	}

	@Test
	public void testCreateAddressValidCEP() throws JsonProcessingException, IOException {
		Address address = address("04325-000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo",
		    State.SP).neighborhood("Vila do Encontro").complement("ap 000").build();

		AddressForm form = new AddressForm(address);

		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());

		// try to create Address.
		JsonNode json = post("address").json(form).expectedStatus(HttpStatus.CREATED).getJson();

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertNotNull("$.id")
		    .assertEquals("$.cep", "04325-000")
		    .assertEquals("$.street", "Avenida Engenheiro Armando de Arruda Pereira")
		    .assertEquals("$.number", "3411")
		    .assertEquals("$.city", "São Paulo")
		    .assertEquals("$.state", "SP")
		    .assertEquals("$.neighborhood", "Vila do Encontro")
		    .assertEquals("$.complement", "ap 000");

		// Guarantee an Address exists.
		assertEquals(1l, addressRepository.count());
	}

	@Test
	public void testCreateDuplicatedAddressValidCEP() throws JsonProcessingException, IOException {
		Address address = address("04325-000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo",
		    State.SP).neighborhood("Vila do Encontro").complement("ap 000").build();

		saveall(address);

		AddressForm form = new AddressForm(address);

		// Guarantee no address exists.
		assertEquals(1l, addressRepository.count());

		// try to create Address.
		JsonNode json = post("address").json(form).expectedStatus(HttpStatus.CONFLICT).getJson();

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertEquals("$.message", "Address for informed cep and street already exists");
	}

	@Test
	public void testCreateAddressInvalidCEP() throws JsonProcessingException, IOException {
		Address address = address("04325-000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo",
		    State.SP).neighborhood("Vila do Encontro").complement("ap 000").build();

		AddressForm form = new AddressForm(address);
		// Invalid CEP
		form.setCep("100000000");

		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());

		// try to create Address.
		JsonNode json = post("address").json(form).expectedStatus(HttpStatus.PRECONDITION_FAILED).getJson();

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertEquals("$.message", "Invalid CEP: 100000000");

		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());
	}

	@Test
	public void testCreateAddressAnotherInvalidCEP() throws JsonProcessingException, IOException {
		Address address = address("04325-000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo",
				State.SP).neighborhood("Vila do Encontro").complement("ap 000").build();
		
		AddressForm form = new AddressForm(address);
		// Invalid CEP
		form.setCep("AJISJASS");
		
		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());
		
		// try to create Address.
		JsonNode json = post("address").json(form).expectedStatus(HttpStatus.PRECONDITION_FAILED).getJson();
		
		JsonAssert.with(json.toString())
		.assertNotNull("$")
		.assertEquals("$.message", "Invalid CEP: AJISJASS");
		
		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());
	}

	@Test
	public void testCreateAddressValidCEPMissingParameter() throws JsonProcessingException, IOException {
		Address address = address("04325-000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo",
		    State.SP).neighborhood("Vila do Encontro").complement("ap 000").build();

		AddressForm form = new AddressForm(address);
		// Missing parameters.
		form.setCity(null);
		form.setStreet(null);

		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());

		// try to create Address.
		JsonNode json = post("address").json(form).expectedStatus(HttpStatus.BAD_REQUEST).getJson();

		System.out.println(json.toString());

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertThat("$.message", Matchers.containsString("city is required"))
		    .assertThat("$.message", Matchers.containsString("street is required"));

		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());
	}

	@Test
	public void testReadAddress() throws JsonProcessingException, IOException {
		Address address = address("04325000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo", State.SP)
		    .build();

		saveall(address);

		// Guarantee an address exists.
		assertEquals(1l, addressRepository.count());

		// try to read Address.
		JsonNode json = get("address/%d", address.getId()).expectedStatus(HttpStatus.OK).getJson();

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertEquals("$.id", address.getId().intValue())
		    .assertEquals("$.cep", "04325-000")
		    .assertEquals("$.street", "Avenida Engenheiro Armando de Arruda Pereira")
		    .assertEquals("$.number", "3411")
		    .assertEquals("$.city", "São Paulo")
		    .assertEquals("$.state", "SP")
		    .assertNull("$.neighborhood")
		    .assertNull("$.complement", "ap 000");
	}

	@Test
	public void testReadAddressNotFound() throws JsonProcessingException, IOException {
		// try to read Address.
		JsonNode json = get("address/1").expectedStatus(HttpStatus.NOT_FOUND).getJson();

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertEquals("$.message", "Address not found.");
	}

	@Test
	public void testUpdateAddressValidCEP() throws JsonProcessingException, IOException {
		Address address = address("04325000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo", State.SP)
		    .build();

		saveall(address);

		// Guarantee an address exists.
		assertEquals(1l, addressRepository.count());

		AddressForm form = new AddressForm(address);
		// Updated parameters.
		form.setCity("Rio de Janeiro");
		form.setStreet("1 de Abril");

		// try to update Address.
		JsonNode json = put("address/%d", address.getId()).json(form).expectedStatus(HttpStatus.OK).getJson();

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertEquals("$.id", address.getId().intValue())
		    .assertEquals("$.cep", "04325-000")
		    .assertEquals("$.street", "1 de Abril")
		    .assertEquals("$.number", "3411")
		    .assertEquals("$.city", "Rio de Janeiro")
		    .assertEquals("$.state", "SP")
		    .assertNull("$.neighborhood")
		    .assertNull("$.complement", "ap 000");

		// Guarantee an address still exists.
		assertEquals(1l, addressRepository.count());
	}

	@Test
	public void testUpdateAddressNotFound() throws JsonProcessingException, IOException {
		Address address = address("04325000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo", State.SP)
		    .build();

		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());

		AddressForm form = new AddressForm(address);

		// try to update Address.
		JsonNode json = put("address/1").json(form).expectedStatus(HttpStatus.NOT_FOUND).getJson();

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertEquals("$.message", "Address not found.");
	}

	@Test
	public void testDeleteAddress() throws JsonProcessingException, IOException {
		Address address = address("04325000", "Avenida Engenheiro Armando de Arruda Pereira", "3411", "São Paulo", State.SP)
		    .build();

		saveall(address);

		// Guarantee an address exists.
		assertEquals(1l, addressRepository.count());

		// try to delete Address.
		JsonNode json = delete("address/%d", address.getId()).expectedStatus(HttpStatus.OK).getJson();

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertEquals("$.id", address.getId().intValue())
		    .assertEquals("$.cep", "04325-000")
		    .assertEquals("$.street", "Avenida Engenheiro Armando de Arruda Pereira")
		    .assertEquals("$.number", "3411")
		    .assertEquals("$.city", "São Paulo")
		    .assertEquals("$.state", "SP")
		    .assertNull("$.neighborhood")
		    .assertNull("$.complement", "ap 000");

		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());
	}

	@Test
	public void testDeleteAddressNotFound() throws JsonProcessingException, IOException {
		// Guarantee no address exists.
		assertEquals(0l, addressRepository.count());

		// try to delete Address.
		JsonNode json = delete("address/1").expectedStatus(HttpStatus.NOT_FOUND).getJson();

		JsonAssert.with(json.toString())
		    .assertNotNull("$")
		    .assertEquals("$.message", "Address not found.");
	}
}