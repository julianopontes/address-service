package com.jop.address.domain;

/**
 * States enumerated.
 * 
 * @author julianopontes
 *
 */
public enum State {

	AC("Acre"),
	AL("Alagoas"),
	AP("Amapá"),
	AM("Amazonas"),
	BA("Bahia"),
	CE("Ceará"),
	DF("Distrito Federal"),
	ES("Espírito Santo"),
	GO("Goiás"),
	MA("Maranhão"),
	MT("Mato Grosso"),
	MS("Mato Grosso do Sul"),
	MG("Minas Gerais"),
	PA("Pará"),
	PB("Paraíba"),
	PR("Paraná"),
	PE("Pernambuco"),
	PI("Piauí"),
	RJ("Rio de Janeiro"),
	RN("Rio Grande do Norte"),
	RS("Rio Grande do Sul"),
	RO("Rondônia"),
	RR("Roraima"),
	SC("Santa Catarina"),
	SP("São Paulo"),
	SE("Sergipe"),
	TO("Tocantins");

	private final String label;

	State(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

//	public String[] getLabels() {
//		State[] values = values();
//		String[] labels = new String[values.length];
//		
//		for (int i = 0; i < values.length; i++) {
//			labels[i] = values[i].label;
//		}
//
//		return labels;
//	}
}