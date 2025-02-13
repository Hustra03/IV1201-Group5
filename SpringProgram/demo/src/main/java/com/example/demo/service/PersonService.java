package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.dto.PersonDTO;
import com.example.demo.domain.entity.Person;
import com.example.demo.repository.PersonRepository;

@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW) //TODO Change to use existing if possible
public class PersonService {
    private final PersonRepository personRepository;
        /**
     * Constructs a new instance of the ServiceExample (Spring boot managed).
     *
     * @param personRepository the repository for accessing person database data
     */
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * This function returns a list of people whose names match the provided string
     * @param name The name to find a match for
     * @return A list of matching people
     */
    public List<? extends PersonDTO> FindPeopleByName(String name){
        return personRepository.findByName(name);
    }

    /**
     * This function adds a new person, with the specified fields
     * @param name the name of the person to add
     * @param surname the surname of the person to add
     */
    public void AddPerson(String name,String surname)
    {
        Person person = new Person();
        person.setName(name);
        person.setSurname(surname);
        personRepository.save(person);
    }
}
