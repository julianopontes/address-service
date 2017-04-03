package com.jop.address;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URI;

import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Auxiliar class for build request.
 * 
 * @author julianopontes
 *
 */
public class RequestBuilder {

	private ObjectMapper mapper = new ObjectMapper();
	private TestRestTemplate rest = new TestRestTemplate();
	private MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
	private MultiValueMap<String, String> queryString = new LinkedMultiValueMap<String, String>();
	private String URI;
	private String server;
	private Object json;
	private HttpMethod method;
	private HttpStatus status;

	/**
	 * Constructor.
	 * 
	 * @param server
	 * @param URI
	 * @param method
	 */
	public RequestBuilder(String server, String URI, HttpMethod method) {
		super();
		this.server = server;
		this.URI = URI;
		this.method = method;
	}

	/**
	 * Add header to request.
	 * 
	 * @param key
	 * @param value
	 * @return RequestBuilder
	 */
	public RequestBuilder header(String key, String value) {
		if (value != null) {
			this.headers.add(key, value);
		}
		return this;
	}
	
	/**
	 * Add query parameter to request.
	 * 
	 * @param key
	 * @param value
	 * @return RequestBuilder
	 */
	public RequestBuilder queryParam(String key, Object value) {
		this.queryString.add(key, value.toString());
		return this;
	}

	/**
	 * Add informed JSON into request body.
	 * 
	 * @param json
	 * @return RequestBuilder
	 */
	public RequestBuilder json(Object json) {
		this.json = json;
		return this;
	}

	/**
	 * Add verification of status code to request result.
	 * 
	 * @param status
	 * @return RequestBuilder
	 */
	public RequestBuilder expectedStatus(HttpStatus status) {
		this.status = status;
		return this;
	}

	/**
	 * Execute request and get response.
	 * 
	 * @param responseType
	 * @return response
	 */
	@SuppressWarnings("rawtypes")
	public <T> ResponseEntity<T> getResponse(Class<T> responseType) {
		HttpEntity entity;
		if (HttpMethod.GET.equals(method)) {
			entity = new HttpEntity<Void>(headers);
		} else {
			try {
				this.header("Content-Type", "application/json;charset=UTF-8");
				entity = new HttpEntity<String>(mapper.writeValueAsString(this.json), headers);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}

		URI link = UriComponentsBuilder.fromHttpUrl(server).path(URI)
		    .queryParams(queryString).build().toUri();

		ResponseEntity<T> response = rest.exchange(link, method, entity, responseType);

		if (status != null) {
			assertThat(response.getStatusCode(), equalTo(status));
		}

		return response;
	}

	/**
	 * Execute request and get response in JSON format.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public JsonNode getJson() throws JsonProcessingException, IOException {
		return mapper.readTree(getResponse(String.class).getBody());
	}

	/**
	 * Execute request and verify if results equals to informed error.
	 * 
	 * @param errorMessage
	 */
	public void errorMessage(String errorMessage) {
		try {
			JsonNode json = this.getJson();
			assertThat(json.get("message").asText(), equalTo(errorMessage));
		} catch (IOException e) {
			throw new RuntimeException("Can't convert response to Json");
		}
	}

	/**
	 * Execute request and get response as object.
	 * 
	 * @return response as object
	 */
	public ResponseEntity<Object> getResponse() {
		return this.getResponse(Object.class);
	}
}