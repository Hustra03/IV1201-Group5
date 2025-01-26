package com.example.demo.domain.entity;

import com.example.demo.domain.dto.PersonDTO;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a person entity in the system.
 */
@Entity
public class Person implements PersonDTO{
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "person_id")
    private Integer id;

    @Column(name="name")
    private String name;

    @Column(name="surname")
    private String surname;

    @Column(name="pnr")
    private String pnr;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @ManyToOne
    private Role role;

    /**
     * Default constructor.
     */
    public Person() {
    }

    /**
     * Implements the PersonDTO function GetId, and returns the person objects id
     * @return the person's id
     */
    @Override
    public Integer GetId() {
        return this.id;
    }

    /**
     * Implements the PersonDTO function GetName, and returns the person objects first name
     * @return the person's first name
     */
    @Override
    public String GetName() {
        return this.name;
    }

    /**
     * Implements the PersonDTO function GetSurname, and returns the person objects last name
     * @return the person's last name
     */
    @Override
    public String GetSurname() {
        return this.surname;
    }

    /**
     * Implements the PersonDTO function GetPnr, and returns the person objects personal identity number
     * @return the person's identity number
     */
    @Override
    public String GetPnr() {
        return this.pnr;
    }

    /**
     * Implements the PersonDTO function GetEmail, and returns the person objects personal email
     * @return the person's email
     */
    @Override
    public String GetEmail() {
        return this.email;
    }

    /**
     * Implements the PersonDTO function GetPassword, and returns the person objects password
     * @return the person's password
     */
    @Override
    public String GetPassword() {
        return this.password;
    }

    /**
     * Implements the PersonDTO function GetRole, and returns the person objects role.
     * @return the person's role
     */
    public Role GetRole() {
        return this.role;
    }

}
