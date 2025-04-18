package com.example.demo.presentation.restControllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.ApplicationStatus;
import com.example.demo.domain.PersonDetails;
import com.example.demo.domain.dto.ApplicationDTO;
import com.example.demo.presentation.restException.InvalidParameterException;
import com.example.demo.service.ReviewService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/review")
@CrossOrigin(origins = "${ALLOWED_ORIGINS:http://localhost:3000}") // This uses the config in config/WebConfig.java to allow cross-origin access
@PreAuthorize("hasAuthority('recruiter')")      //only recruiters can access the review page
/**
 * This endpoint controller is responsible for handeling the requests concerning
 * reviewing application.
 * This includes, for example, returning all application along with updating an applications status
 */
public class ReviewerEndpointController {
    @Autowired
    private final ReviewService reviewService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewerEndpointController.class.getName()); 


    /**
     * Constructs a new instance of the ReviewerEndpointController (this is
     * Spring boot managed).
     * 
     * @param reviewService The service used to handle review related manners.
     */
    public ReviewerEndpointController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * This function returns a list of applications
     * @param authentication This contains the pre-authorized authentication for the user
     * @return This function will return the list of existing applications as a json object to the user
     */
    @GetMapping("/getApplications")
    public List<? extends ApplicationDTO> GetApplications() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("All applications requested by (`{}`)",currentUser);
        List<? extends ApplicationDTO> applications = reviewService.GetApplications();
        return applications;
    }

    /**
     * This function returns the existing standard competences
     * 
     * @param status the application status to find applications for
     * @throws InvalidParameterException this exceptions is thrown is a parameter is incorrectly specified
     * @return This function will return the list of existing applications with the matching status as a json object to the user
     */
    @GetMapping("/getApplicationsByStatus/{status}")
    public List<? extends ApplicationDTO> GetApplicationsByStatus(@PathVariable String status) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("All applications with status (`{}`) requested by (`{}`)" ,status,currentUser);

        ApplicationStatus parsedApplicationStatus=null;
        try {
            parsedApplicationStatus=ApplicationStatus.valueOf(status.toLowerCase());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to retrive applications with status (`{}`) since that is invalid application status",status);
            throw new InvalidParameterException("Provided value ("+status+") is not valid value for application status, please specify as \"unchecked\",\"accepted\" or \"denied\"");
        }
        catch(NullPointerException e) {
            LOGGER.error("Failed to retrive applications with status (`{}`) since that is null",status);
            throw new InvalidParameterException("Provided status value is null, please specify as \"unchecked\",\"accepted\" or \"denied\"");
        }
        catch(Exception e) {
            LOGGER.error("Failed to retrive applications with status (`{}`) due to unknown error related to application status",status);
            throw new InvalidParameterException("Unknown cause, this should never occur, but double check formating of request, specifically for status parameter");
        }

        List<? extends ApplicationDTO> applications = reviewService.GetApplicationsByStatus(parsedApplicationStatus);
        return applications;
    }

    /**
     * This function returns the specified application
     * 
     * @param id the application id to find application
     * @throws InvalidParameterException this exceptions is thrown is a parameter is incorrectly specified
     * @return This function will return the application of the application id
     */
    @GetMapping("/getApplicationsById/{id}")
    public ApplicationDTO GetApplicationsById(@PathVariable String id) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Application with ID (`{}`) requested by (`{}`)" ,id,currentUser);

        Integer parsedApplicationId=null;
        try {
            parsedApplicationId=Integer.parseInt(id);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to retrive application with id (`{}`) since id is invalid integer",id);
            throw new InvalidParameterException("Provided value ("+id+") could not be parsed as a valid integer" );
        }
        catch(Exception e) {
            LOGGER.error("Failed to retrive application with id (`{}`) due to unknown error",id);
            throw new InvalidParameterException("Unknown cause, but double check formating of request, specifically for the applicationId parameter");
        }

        ApplicationDTO application = reviewService.GetApplicationsById(parsedApplicationId);
        return application;
    }

    /**
     * This function updates the application status of a specific application
     * 
     * @param applicationId the application id
     * @param status the new application status to set the application to
     * @param versionNumber the application version
     * @throws InvalidParameterException this exceptions is thrown is a parameter is incorrectly specified
     * @return The updated application as a json object
     */
    @PostMapping("/updateApplicationStatus")
    public ApplicationDTO UpdateApplicationsByStatus(@RequestParam String applicationId,@RequestParam String status,@RequestParam String versionNumber) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Status change for application with id (`{}`) version number (`{}`) to (`{}`) requested by (`{}`)",applicationId,versionNumber,status,currentUser);

        ApplicationStatus parsedApplicationStatus=null;
        try {
            parsedApplicationStatus=ApplicationStatus.valueOf(status.toLowerCase());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to update application with id (`{}`) version number (`{}`) to (`{}`) since application status is invalid",applicationId,versionNumber,status);
            throw new InvalidParameterException("Provided value ("+status+") is not valid value for application status, please specify as \"unchecked\",\"accepted\" or \"denied\"");
        }
        catch(NullPointerException e) {
            LOGGER.error("Failed to update application with id (`{}`) version number (`{}`) to (`{}`) since application status is null",applicationId,versionNumber,status);
            throw new InvalidParameterException("Provided status value is null, please specify as \"unchecked\",\"accepted\" or \"denied\"");
        }
        catch(Exception e) {
            LOGGER.error("Failed to update application with id (`{}`) version number (`{}`) to (`{}`) due to unknown error related to application status",applicationId,versionNumber,status);
            throw new InvalidParameterException("Unknown cause, but double check formating of request, specifically for the status parameter");
        }

        Integer parsedApplicationId=null;
        try {
            parsedApplicationId=Integer.parseInt(applicationId);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to update application with id (`{}`) version number (`{}`) to (`{}`) since id is invalid integer",applicationId,versionNumber,status);
            throw new InvalidParameterException("Provided value ("+applicationId+") could not be parsed as a valid integer" );
        }
        catch(Exception e) {
            LOGGER.error("Failed to update application with id (`{}`) version number (`{}`) to (`{}`) due to unknown error related to id",applicationId,versionNumber,status);
            throw new InvalidParameterException("Unknown cause, but double check formating of request, specifically for the applicationId parameter");
        }

        Integer parsedVersionNumber=null;
        try {
            parsedVersionNumber=Integer.parseInt(versionNumber);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to update application with id (`{}`) version number (`{}`) to (`{}`) since version number is invalid integer",applicationId,versionNumber,status);
            throw new InvalidParameterException("Provided value ("+versionNumber+") could not be parsed as a valid integer" );
        }
        catch(Exception e) {
            LOGGER.error("Failed to update application with id (`{}`) version number (`{}`) to (`{}`) due to unknown error related to version number",applicationId,versionNumber,status);
            throw new InvalidParameterException("Unknown cause, but double check formating of request, specifically for the version number parameter");
        }
        
        ApplicationDTO applications = reviewService.SetApplicationStatus(parsedApplicationId,parsedApplicationStatus,parsedVersionNumber);
        return applications;
    }

}
