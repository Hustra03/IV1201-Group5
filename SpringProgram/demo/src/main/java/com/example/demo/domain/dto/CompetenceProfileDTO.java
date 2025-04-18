package com.example.demo.domain.dto;

/** 
 * The CompetenceProfileDTO interface provides a data transfer interface for handling competence profile objects
*/

public interface CompetenceProfileDTO {
    /** Interface function for retriving a competence profile's id
     * 
     * @return competence profile's id
     */
    public Integer getCompetenceProfileId();

    /** Interface function for retriving the person who this competence profile corresponds to
     * 
     * @return the person the competence profile belongs to
     */
    public PersonDTO getPerson();

    /** Interface function for retriving the competence who this competence profile explains
     * 
     * @return the competence the competence profile explains
     */
    public CompetenceDTO getCompetenceDTO();

    /** Interface function for retriving the years of experience this profile states the person has
     * 
     * @return the number of years of experience
     */
    public double getYearsOfExperience();
}
