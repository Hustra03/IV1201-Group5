package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.entity.Competence;
import com.example.demo.domain.entity.CompetenceProfile;
import com.example.demo.domain.entity.Person;

import org.springframework.transaction.annotation.Propagation;


/**
 * The CompetenceProfileRepository interface is responsible for providing CRUD operations for the CompetenceProfile entity.
 * Any explicitly defined functions can only be called inside a transaction, however JPARepository inherited functions can be called outside of one
 * This should however not be done, since doing so could risk a rollback not being performed correctly
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
//Note that the above means that any explicitly defined methods for this interface requier being called inside a transaction
//Non-explicitly defined ones, aka inherited ones from JPARepository do not requier this, however note that they should still be called inside of a transaction to avoid rollback issues
public interface CompetenceProfileRepository extends JpaRepository<CompetenceProfile, Integer> {
    /**
     * This interface tells JPA to generate a query to find a list of CompetenceProfile for a specific person
     * @param person the person status to find competence profiles for
     * @return A list of matching competence profiles
     */
    List<CompetenceProfile> findAllByPerson(Person person);

    /**
     * This interface function tells JPA to generate a query which confirms if a CompetenceProfile already exists with these exact values
     * @param person the person the competence profile is for
     * @param competence the competence this profile describes
     * @param yearsOfExperience the years of experience the individual has for this period
     * @return a boolean for if a matching competence profile exists or not
     */
    boolean existsByPersonAndCompetenceAndYearsOfExperience(Person person, Competence competence, Double yearsOfExperience);
}
