package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static  org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;





@SpringBootTest
@AutoConfigureMockMvc
public class DemoApplicationTests {
	@Autowired
	PersonRepository rep;
	
	@Autowired MockMvc mockMvc;
	
	@Test
	public void shouldCreatePerson() {
		PersonController pCont = new PersonController(rep);
		Person person = new Person();
		person.setName("name");
		person.setAdress("addr");
		person.setEmail("aaaaaaaaa@bbbbbbb@ccc");
		person.setGender("male");
		person.setPhone("+212 000000000");
		Person addedPerson = pCont.addPerson(person);
		assertNotNull(rep.findById(addedPerson.getIdentity()));
	}
	
	@Test
	public void shouldReadAllPersons() {
		PersonController pCont = new PersonController(rep);
		List<Person> list = pCont.getPersons();
		assertThat(list.size() == rep.count());
	}
	
	@Test
	public void shouldReadPersonById() {
		PersonController pCont = new PersonController(rep);
		long id = 12;
		Optional<Person> readedPerson = pCont.getPerson(id);
		long idReturned = readedPerson.get().getIdentity();
		assertThat(id == idReturned);
	}
	
	@Test
	public void shouldDeletePersonById() {
		PersonController pCont = new PersonController(rep);
		long id = rep.findAll().get(0).getIdentity();
		pCont.deletePerson(id);
		assertThat(rep.existsById(id) == false);
	}
	
	@Test
	public void shouldUpdatePersonById() {
		PersonController pCont = new PersonController(rep);
		long id = rep.findAll().get(0).getIdentity();
		Person person = new Person();
		person.setName("name");
		person.setAdress("addr");
		person.setEmail("aaaaaaaaa@bbbbbbb@ccc");
		person.setGender("male");
		person.setPhone("+212 000000000");
		pCont.updatePerson(id, person);
		Person updatedPerson = pCont.getPerson(id).get();
		boolean test = (updatedPerson.getName() == person.getName() &&
				updatedPerson.getGender() == person.getGender() &&
				updatedPerson.getEmail() == person.getEmail() &&
				updatedPerson.getPhone() == person.getPhone() &&
				updatedPerson.getAdress() == person.getAdress()
				);
		assertThat(test);
	}
	
	@Test
	public void shouldRaiseHttpExceptionWhenDeletingPersonIfPersonNotFound() {
		PersonController pCont = new PersonController(rep);
		long id = 10000000;
		ResponseStatusException throwen = assertThrows(ResponseStatusException.class, () -> pCont.deletePerson(id), "expected to throw ResponseStatusException but it didn't");
		assertEquals(String.format("204 NO_CONTENT \"person with id %d not found\"", id), throwen.getMessage());
	}
	
	@Test
	public void shouldRaiseHttpExceptionWhenReadingPersonIfPersonNotFound() {
		
		PersonController pCont = new PersonController(rep);
		long id = 10000000;
		ResponseStatusException throwen = assertThrows(ResponseStatusException.class, () -> pCont.getPerson(id), "expected to throw ResponseStatusException but it didn't");
		assertEquals(String.format("204 NO_CONTENT \"person with id %d not found\"", id), throwen.getMessage());
	}
	@Test
	public void shouldRaiseHttpExceptionWhenUpdatingPersonIfPersonNotFound() {
		PersonController pCont = new PersonController(rep);
		Person person = new Person();
		person.setName("name");
		person.setAdress("addr");
		person.setEmail("aaaaaaaaa@bbbbbbb@ccc");
		person.setGender("male");
		person.setPhone("+212 000000000");
		long id = 10000000;
		ResponseStatusException throwen = assertThrows(ResponseStatusException.class, () -> pCont.updatePerson(id, person), "expected to throw ResponseStatusException but it didn't");
		assertEquals(String.format("204 NO_CONTENT \"person with id %d not found\"", id), throwen.getMessage());
	}
	
	@Test
	public void testAuthWithIncorrectCredencials() throws Exception {
		String json = "{\"username\": \"fakeuser\", \"password\": \"fakepassword\"}";
		mockMvc.perform(post("http://localhost:8080/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().is(400))
				.andExpect(status().reason("incorrect username or password"));
	}
	
	@Test
	public void testAuthWithcorrectCredencials() throws Exception {
		String json = "{\"username\": \"user\", \"password\": \"password\"}";
		mockMvc.perform(post("http://localhost:8080/api/auth").contentType(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().is(200));
	}
	
	@Test
	public void useGetPersonsApiWithoutAuthentication() throws Exception {
		mockMvc.perform(get("http://localhost:8080/api/person"))
		.andExpect(status().is(403));
	}
	
	@Test
	public void useGetPersonByIdApiWithoutAuthentication() throws Exception {
		long id = rep.findAll().get(0).getIdentity();
		mockMvc.perform(get("http://localhost:8080/api/person/"+id))
		.andExpect(status().is(403));
	}
	
	@Test
	public void useDeletePersonByIdApiWithoutAuthentication() throws Exception {
		long id = rep.findAll().get(0).getIdentity();
		mockMvc.perform(delete("http://localhost:8080/api/person/"+id))
		.andExpect(status().is(403));
	}
	
	@Test
	public void useUpdatePersonByIdApiWithoutAuthentication() throws Exception {
		long id = rep.findAll().get(0).getIdentity();
		mockMvc.perform(put("http://localhost:8080/api/person/"+id))
		.andExpect(status().is(403));
	}
	
	@Test
	public void useAddPersonApiWithoutAuthentication() throws Exception {
		mockMvc.perform(post("http://localhost:8080/api/person/"))
		.andExpect(status().is(403));
	}
	
	@Test
	public void accessToGetPersonsApiWithAuthentication() throws Exception {
		
		String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjU5MDgwODk0LCJpYXQiOjE2NTkwNDQ4OTR9.Pq4kMidF7QDvr5yw_YfCRHDXolxUaN5RSxBgEzO981M";
		System.out.println(jwt);
		mockMvc.perform(get("http://localhost:8080/api/person").header("Authorization", "Bearer "+jwt))
			.andExpect(status().is(200));
	}
	@Test
	public void accessToGetPersonByIdApiWithAuthentication() throws Exception {
		long id = rep.findAll().get(0).getIdentity();
		String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjU5MDgwODk0LCJpYXQiOjE2NTkwNDQ4OTR9.Pq4kMidF7QDvr5yw_YfCRHDXolxUaN5RSxBgEzO981M";
		System.out.println(jwt);
		mockMvc.perform(get("http://localhost:8080/api/person/"+id).header("Authorization", "Bearer "+jwt))
			.andExpect(status().is(200));
	}
	
	@Test
	public void accessToPersonPersonByIdApiWithAuthentication() throws Exception {
		long id = rep.findAll().get(0).getIdentity();
		String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjU5MDgwODk0LCJpYXQiOjE2NTkwNDQ4OTR9.Pq4kMidF7QDvr5yw_YfCRHDXolxUaN5RSxBgEzO981M";
		System.out.println(jwt);
		mockMvc.perform(delete("http://localhost:8080/api/person/"+id).header("Authorization", "Bearer "+jwt))
			.andExpect(status().is(200));
	}
	
	@Test
	public void accessTosavePersonApiWithAuthentication() throws Exception {
		String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjU5MDgwODk0LCJpYXQiOjE2NTkwNDQ4OTR9.Pq4kMidF7QDvr5yw_YfCRHDXolxUaN5RSxBgEzO981M";
		String json = "{\r\n"
				+ "\r\n"
				+ "    \"name\":\"FULL NAME\",\r\n"
				+ "\r\n"
				+ "	\"gender\":\"MALE/FEMALE\",\r\n"
				+ "\r\n"
				+ "	\"email\":\"username@gmail.com\",\r\n"
				+ "\r\n"
				+ "	\"phone\" :\"06222222222\",\r\n"
				+ "\r\n"
				+ "	\"adress\" : \"imm 10 appt 5 hay farah casablanca\"\r\n"
				+ "}";
		mockMvc.perform(post("http://localhost:8080/api/person/").header("Authorization", "Bearer "+jwt)
				.contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().is(201));
	}

	@Test
	public void accessToUpdatePersonApiWithAuthentication() throws Exception {
		long id = rep.findAll().get(0).getIdentity();
		String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjU5MDgwODk0LCJpYXQiOjE2NTkwNDQ4OTR9.Pq4kMidF7QDvr5yw_YfCRHDXolxUaN5RSxBgEzO981M";
		String json = "{\r\n"
				+ "\r\n"
				+ "    \"name\":\"FULL NAME\",\r\n"
				+ "\r\n"
				+ "	\"gender\":\"MALE/FEMALE\",\r\n"
				+ "\r\n"
				+ "	\"email\":\"username@gmail.com\",\r\n"
				+ "\r\n"
				+ "	\"phone\" :\"06222222222\",\r\n"
				+ "\r\n"
				+ "	\"adress\" : \"imm 10 appt 5 hay farah casablanca\"\r\n"
				+ "}";
		mockMvc.perform(put("http://localhost:8080/api/person/"+id).header("Authorization", "Bearer "+jwt)
				.contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().is(200));
	}
}