package com.example.demo.domain.entity;

import com.example.demo.domain.dto.CompetenceDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents a competence entity in the system.
 * Implements the CompetenceDTO interface.
 */
@Entity
public class Competence implements CompetenceDTO{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "competence_id")
    private Integer competenceId;

    @Column(name="name")
    @NotBlank(message = "Each competence must have a non-blank name")
    @NotNull(message="Competence name can not be null")
    private String name;

    /**
     * Implements the CompetenceDTO function getCompetenceId, and returns the competence's id
     * @return the competence's id
     */
    @Override
    public Integer getCompetenceId() {
        return this.competenceId;
    }

    /**
     * Implements the CompetenceDTO function getName, and returns the competence's name
     * @return the competence's name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Set the competence's id.
     * 
     * @param id the competence id to set
     */
    public void setId(Integer id)
    {this.competenceId=id;}

    /**
     * Set the competence's name.
     * 
     * @param name the competence name to set
     */
    public void setName(String name)
    {this.name=name;}
}
