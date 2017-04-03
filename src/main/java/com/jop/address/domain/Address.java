package com.jop.address.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * CEP Representation.
 * 
 * @author julianopontes
 *
 */
@Entity
@Table(name = "ADDRESS", uniqueConstraints = @UniqueConstraint(name = "UK_ADDRESS", columnNames = { "CEP", "STREET" }))
@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Address implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6672299592752509992L;

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Column(name = "CEP", nullable = false)
	private Integer cep;

	@Column(name = "STREET", nullable = false, length = 255)
	private String street;

	@Column(name = "NUMBER", nullable = false, length = 30)
	private String number;

	@Column(name = "CITY", nullable = false, length = 255)
	private String city;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATE", nullable = false, length = 2)
	private State state;

	@Column(name = "NEIGHBORHOOD", length = 255)
	private String neighborhood;

	@Column(name = "COMPLEMENT", length = 255)
	private String complement;
}