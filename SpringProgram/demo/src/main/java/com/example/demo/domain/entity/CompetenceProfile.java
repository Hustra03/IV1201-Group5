package com.example.demo.domain.entity;

import java.math.BigDecimal;

import com.example.demo.domain.dto.CompetenceDTO;
import com.example.demo.domain.dto.CompetenceProfileDTO;
import com.example.demo.domain.dto.PersonDTO;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;

/**
 * Represents a competence profile entity in the system.
 * Implements the CompetenceProfileDTO interface.
 */
@Entity
public class CompetenceProfile implements CompetenceProfileDTO{

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "competence_profile_id")
    private Integer competenceProfileId;

    @JoinColumn(name="person_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person person;

    @JoinColumn(name="competence_id", referencedColumnName = "competence_id")
    @ManyToOne
    private Competence competence;

    @Column
    @Min(value =0L,message="The value must be non-negative") //Test for jakarta validation
    @Digits(integer=4,fraction=2) //This should ensure the value can only have the format (xxxx.xx) 
    private BigDecimal yearsOfExperience;

    /**
     * Implements the CompetenceProfileDTO function getCompetenceProfileId, and returns the object's id
     * @return the competence profile's id
     */
    @Override
    public Integer getCompetenceProfileId() {
        return this.competenceProfileId;
    }

    /**
     * Implements the CompetenceProfileDTO function getPerson, and returns the person who this profile belongs to
     * @return the competence profile's id
     */
    @Override
    public Person getPerson() {
        return this.person;
    }

    /**
     * Implements the CompetenceProfileDTO function getCompetenceDTO, and returns the competence this profile corresponds to
     * @return the competence
     */
    @Override
    public Competence getCompetenceDTO() {
        return this.competence;
    }

    /**
     * Implements the CompetenceProfileDTO function getYearsOfExperience, and returns the years of experience this profile corresponds to
     * @return the years of experience
     */
    @Override
    public BigDecimal getYearsOfExperience() {
        return this.yearsOfExperience;
    }
    
}
