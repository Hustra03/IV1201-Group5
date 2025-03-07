package com.example.demo.domain.entity;

import com.example.demo.domain.dto.LanguageDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
/**
 * Represents a language entity in the system.
 * Implements the LanguageDTO interface.
 */
public class Language implements LanguageDTO{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    private Integer id;

    //Note this will ensure new languages added have unique names, if the table is created using JPA, otherwise this needs to be specified in the SQL create table statement
    //Link to stackoverflow topic about this: https://stackoverflow.com/questions/3496028/columnunique-true-does-not-seem-to-work 
    @Column(name="name")
    @NotNull(message = "Language name can not be null")
    @NotBlank(message = "Language name can not be blank")
    private String name;

    /**
     * Implements the LanguageDTO function getLanguageId, and returns the language's id
     * @return the language's id
     */
    @Override
    public Integer getLanguageId() {
        return this.id;
    }

    /**
     * Implements the LanguageDTO function getLanguageName, and returns the language's name
     * @return the language's name
     */
    @Override
    public String getLanguageName() {
        return this.name;
    }
    
    /**
     * A simple setter for the language's name
     * @param name the new name for this object
     */
    public void SetLanguageName(String name){this.name=name;}

    
    /**
     * A simple setter for the language's id
     * Note that this should only be used for testing purposes, and any use in production code will result in an exception!
     * @param id the new id for this object
     */
    public void SetLanguageId(Integer id){this.id=id;}
}
