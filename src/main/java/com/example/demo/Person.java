package com.example.demo;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class Person {
	@Id
	@GeneratedValue
	private Long identity;
	
	private String name;
	private String gender;

	private String email;

	private String phone;

	private String adress;
	

	public Person() {
		// TODO Auto-generated constructor stub
	}

	public Long getIdentity() {
		return identity;
	}

	public void setIdentity(Long identity) {
		this.identity = identity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public Person(Long id, String name, String gender, String email, String phone, String adress) {
		super();
		this.identity = id;
		this.name = name;
		this.gender = gender;
		this.email = email;
		this.phone = phone;
		this.adress = adress;
	}
}
