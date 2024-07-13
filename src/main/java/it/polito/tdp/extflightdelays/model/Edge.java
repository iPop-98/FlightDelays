package it.polito.tdp.extflightdelays.model;

public class Edge {
	
	private Airport v1;
	private Airport v2;
	private Integer peso;
	
	
	public Edge(Airport v1, Airport v2, Integer peso) {
		super();
		this.v1 = v1;
		this.v2 = v2;
		this.peso = peso;
	}


	public Airport getV1() {
		return v1;
	}


	public Airport getV2() {
		return v2;
	}


	public Integer getPeso() {
		return peso;
	}
	
	

}
