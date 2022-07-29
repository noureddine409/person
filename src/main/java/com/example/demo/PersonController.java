package com.example.demo;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.security.MyUserDetailsService;
import com.example.demo.security.authentication.AuthenticationRequest;
import com.example.demo.security.authentication.AuthenticationResponse;
import com.example.demo.security.authentication.JwtUtil;


@RestController
public class PersonController {
	
	private PersonRepository personRepository ;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private MyUserDetailsService userDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	public PersonController(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@GetMapping("/api")
	@ResponseStatus(HttpStatus.OK)
	public String root() {
		return "hello world";
	}
	
	@GetMapping("api/person/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Optional<Person> getPerson(@PathVariable("id") Long id) {
		if(personRepository.existsById(id))
			return personRepository.findById(id);
		else
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, String.format("person with id %d not found", id));
	}
	
	@GetMapping("api/person")
	@ResponseStatus(HttpStatus.OK)
	public List<Person> getPersons(){
		return personRepository.findAll();
	}
	
	@PostMapping("api/person")
	@ResponseStatus(HttpStatus.CREATED)
	public Person addPerson(@RequestBody Person person) {
		personRepository.save(person);
		return person;
	}
	
	@PutMapping("api/person/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Person updatePerson(@PathVariable("id") Long id, @RequestBody Person person) {
		if(personRepository.existsById(id)) {
			person.setIdentity(id);
			personRepository.save(person);
			return person;
		}
		else throw new ResponseStatusException(HttpStatus.NO_CONTENT, String.format("person with id %d not found", id));
	}
	
	@DeleteMapping("api/person/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deletePerson(@PathVariable("id") Long id) {
		if(personRepository.existsById(id))
			personRepository.deleteById(id);
		else throw new ResponseStatusException(HttpStatus.NO_CONTENT, String.format("person with id %d not found", id));
	}
	
	@PostMapping("api/auth")
	public ResponseEntity<AuthenticationResponse> authenicate(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
					);
		}
		catch(BadCredentialsException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"incorrect username or password");
		}
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		final String jwt = jwtUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
}
