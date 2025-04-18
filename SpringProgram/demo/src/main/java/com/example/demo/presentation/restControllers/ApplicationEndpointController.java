package com.example.demo.presentation.restControllers;

import java.sql.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.PersonDetails;
import com.example.demo.domain.dto.ApplicationDTO;
import com.example.demo.domain.dto.AvailabilityDTO;
import com.example.demo.domain.dto.CompetenceProfileDTO;
import com.example.demo.domain.requestBodies.ApplicationSubmissionRequestBody;
import com.example.demo.presentation.restException.InvalidParameterException;
import com.example.demo.service.ApplicationService;

@RestController
@RequestMapping("/application")
@PreAuthorize("hasAuthority('applicant')")
@CrossOrigin(origins = "${ALLOWED_ORIGINS:http://localhost:3000}") // This uses the config in config/WebConfig.java to allow cross-origin access
/**
 * This endpoint controller is responsible for handeling the requests concerning submitting an application.
 * This includes, for example, creating a new competence profile for a specific user along with submitting an application
 */
public class ApplicationEndpointController {
    @Autowired
    private final ApplicationService applicationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewerEndpointController.class.getName()); 

    /**
     * Constructs a new instance of the ApplicationEndpointController (this is Spring boot managed).
     * 
     * @param applicationService The service used to handle application related manners.
     */
    public ApplicationEndpointController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * This function returns a list of competence profiles for a specific person
     * @return If no exception is thrown, the list of competence profiles
     */
    @GetMapping("/getAllCompetenceProfiles")
    public List<? extends CompetenceProfileDTO> GetCompetenceProfilesForAPerson()
    {
        PersonDetails userAuthentication=((PersonDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        LOGGER.info("List of competence profiles for person (`{}`) requested by user ('{}')", userAuthentication.getUsername());

        return applicationService.GetCompetenceProfilesForAPerson(userAuthentication.getPersonId());
    }

    /**
     * This function creates a new competence profile based on provided info regarding personId, competenceId and years of experience
     * 
     * @param competenceId This is the competence this profile was created for
     * @param yearsOfExperience This is the number of years of experience the person claims to have
     * @return If no exception is thrown, this will return the newly create competence profile as an object
     */
    @PostMapping("/createCompetenceProfile")
    public CompetenceProfileDTO CreateCompetenceProfile(@RequestParam String competenceId,@RequestParam String yearsOfExperience)
    {
        PersonDetails userAuthentication=((PersonDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        LOGGER.info("Creation of competence profile for user with id (`{}`) for competence with id (`{}`) with (`{}`) years of experience requested by user ('{}')",userAuthentication.getPersonId(),competenceId,yearsOfExperience,userAuthentication.getUsername()); 

        Integer parsedCompetenceId=null;
        try {
            parsedCompetenceId=Integer.parseInt(competenceId);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed creation of competence profile for user with id (`{}`) for competence with id (`{}`) with (`{}`) years of experience since competence id is invalid integer",userAuthentication.getPersonId(),competenceId,yearsOfExperience);
            throw new InvalidParameterException("Provided value ("+competenceId+") could not be parsed as a valid integer" );
        }
        catch(Exception e) {
            LOGGER.error("Failed creation of competence profile for user with id (`{}`) for competence with id (`{}`) with (`{}`) years of experience due to unknown error related to competence id",userAuthentication.getPersonId(),competenceId,yearsOfExperience);
            throw new InvalidParameterException("Unknown cause, but double check formating of request, specifically for the competence id parameter");
        }

        Double parsedYearsOfExperience=null;
        try {
            parsedYearsOfExperience=Double.parseDouble(yearsOfExperience);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to retrive competence with id (`{}`) since that is invalid integer",yearsOfExperience);
            throw new InvalidParameterException("Provided value ("+yearsOfExperience+") could not be parsed as a valid double" );
        }
        catch(Exception e) {
            LOGGER.error("Failed creation of competence profile for user with id (`{}`) for competence with id (`{}`) with (`{}`) years of experience due to unknown error related to years of experience ",userAuthentication.getPersonId(),competenceId,yearsOfExperience);
            throw new InvalidParameterException("Unknown cause, but double check formating of request, specifically for the yearsOfExperience parameter");
        }

        return applicationService.CreateCompetenceProfile(parsedCompetenceId, userAuthentication.getPersonId(), parsedYearsOfExperience);
    }


    /**
     * This function returns a list of a person's availability periods
     * @return If no exception is thrown, a list of availability periods for that person is returned
     */
    @GetMapping("/getAllAvailability")
    public List<? extends AvailabilityDTO> GetAllAvailability()
    {
        PersonDetails userAuthentication=((PersonDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        LOGGER.info("List of availability periods for person (`{}`) requested by uuser ('{}')"  ,userAuthentication.getPersonId(), userAuthentication.getUsername());

        return applicationService.GetAvailabilityForAPerson(userAuthentication.getPersonId());
    }

    /**
     * This function creates a new availability period for a specific person
     * @param fromDate The start of this availability period
     * @param toDate The end of this availability period
     * @return If no exception is thrown, the newly created availability period is returned
     */
    @PostMapping("/createAvailability")
    public AvailabilityDTO CreateAvailability(@RequestParam String fromDate,@RequestParam String toDate)
    {
        PersonDetails userAuthentication=((PersonDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        LOGGER.info("Creation of availability period for person (`{}`) from (`{}`) to (`{}`) requested by user ('{}')",userAuthentication.getPersonId(),fromDate,toDate, userAuthentication.getUsername()); 

        //Link for java.sql.date info https://docs.oracle.com/javase/8/docs/api/java/sql/Date.html#valueOf-java.lang.String- 

        Date parsedFromDate=null;
        try {
            parsedFromDate=Date.valueOf(fromDate);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to create availability for person (`{}`) from (`{}`) to (`{}`) since from date is not a date",userAuthentication.getPersonId(),fromDate,toDate);
            throw new InvalidParameterException("Provided value ("+fromDate+") could not be parsed as a valid date, please use the yyyy-(m)m-(d)d format, with the (m) and (d) specifying that these can be 0 or ignored" );
        }
        catch(Exception e) {
            LOGGER.error("Failed to create availability for person (`{}`) from (`{}`) to (`{}`) due to unknown error related to from date",userAuthentication.getPersonId(),fromDate,toDate);
            throw new InvalidParameterException("Unknown cause, but double check formating of request, specifically for the from date parameter");
        }

        Date parsedToDate=null;
        try {
            parsedToDate=Date.valueOf(toDate);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to create availability for person (`{}`) from (`{}`) to (`{}`) since to date is not a date",userAuthentication.getPersonId(),fromDate,toDate);
            throw new InvalidParameterException("Provided value ("+toDate+") could not be parsed as a valid date, please use the yyyy-(m)m-(d)d format, with the (m) and (d) specifying that these can be 0 or ignored" );
        }
        catch(Exception e) {
            LOGGER.error("Failed to create availability for person (`{}`) from (`{}`) to (`{}`) due to unknown error related to to date",userAuthentication.getPersonId(),fromDate,toDate);
            throw new InvalidParameterException("Unknown cause, but double check formating of request, specifically for the to date parameter");
        }

        return applicationService.CreateAvailability(userAuthentication.getPersonId(), parsedFromDate, parsedToDate);
    }

    /**
     * This function 
     * @param requestBody
     * To give an example of the request body, the following is a basic example of how it could look:
     * <p>{
     *   "availabilityIds":[20872],
     *   "competenceProfileIds":[6488]
     *   }<p>
     * @return If it does not throw an exception, the newly created application is returned
     */
    @PostMapping("/submitApplication")
    public ApplicationDTO SubmitApplication(@RequestBody ApplicationSubmissionRequestBody requestBody)
    {
        PersonDetails userAuthentication=((PersonDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String currentUser = userAuthentication.getUsername();
        LOGGER.info("Creation of application for person (`{}`) requested by user ('{}')" ,userAuthentication.getPersonId(), currentUser); 
        return applicationService.SubmitApplication(userAuthentication.getPersonId(),requestBody.getAvailabilityIds(),requestBody.getCompetenceProfileIds());
    }
}
