package com.example.demo.presentation.restControllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.PersonDetails;
import com.example.demo.domain.dto.CompetenceDTO;
import com.example.demo.domain.dto.CompetenceTranslationDTO;
import com.example.demo.domain.dto.LanguageDTO;
import com.example.demo.presentation.restException.InvalidParameterException;
import com.example.demo.service.TranslationService;

@RestController
@RequestMapping("/translation")
@CrossOrigin(origins = "${ALLOWED_ORIGINS:http://localhost:3000}") // This uses the config in config/WebConfig.java to allow cross-origin access
/**
 * This endpoint controller is responsible for handeling the requests concerning
 * translation.
 * This includes, for example, handeling finding the competence translations for
 * a specific language
 */
public class TranslationEndpointController {
    @Autowired
    private final TranslationService translationService;

    //We create a logger
    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationEndpointController.class.getName()); 

    /**
     * Constructs a new instance of the TranslationEndpointController (this is
     * Spring boot managed).
     * 
     * @param translationService The service used to handle translation related
     *                           manners.
     */
    public TranslationEndpointController(TranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * This function returns the existing standard competences
     * 
     * @return This function will return the list of existing competences as a json
     *         object to the user
     */
    @GetMapping("/getStandardCompetences")
    public List<? extends CompetenceDTO> GetStandardCompetences() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Standard competences requested by user (`{}`)",currentUser); 
        List<? extends CompetenceDTO> existingCompetences = translationService.GetCompetences();
        return existingCompetences;
    }

    /**
     * This function returns a competences specified using its id. If it exists it will be returned, if not a 404 error will be sent stating this.
     * 
     * @param id This is the id for the competence this requests
     * @throws InvalidParameterException if the provided id can not be parsed as an integer 
     * @return This function will return the list of existing competences as a JSON
     *         object to the user
     */
    @GetMapping("/getSpecificCompetence/{id}")
    public CompetenceDTO GetSpecificCompetence(@PathVariable String id) throws InvalidParameterException {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Specific competences with id (`{}`) requested by user (`{}`)" ,id, currentUser); 
        Integer parsedId=null;
        try {
            parsedId=Integer.parseInt(id);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to retrive competence with id (`{}`) since that is invalid integer",id);
            throw new InvalidParameterException("Provided value ("+id+") could not be parsed as a valid integer" );
        }
        catch(Exception e) {
            LOGGER.error("Failed to retrive competence with id (`{}`) due to unknown error",id);
            throw new InvalidParameterException("Unknown cause, but double check formating of request, specifically for the applicationId parameter");
        }
        return translationService.GetSpecificCompetence(parsedId);
    }

    /**
     * This function returns a list of competence translations which are in a specified language. If any exists they will be returned, if not a 404 error will be sent stating this and if the language was missing or if the translations did not exist.
     * 
     * @param language This is the language the translations are for
     * @return This function will return the list of competences translations as a json object to the user, if no error occurs
     */
    @GetMapping("/getCompetenceTranslation")
    public List<? extends CompetenceTranslationDTO> GetCompetenceTranslation(@RequestParam String language) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Competence translation for language (`{}`) requested by user (`{}`)",language, currentUser); 
        return translationService.GetCompetenceTranslation(language.toLowerCase());
    }

    /**
     * Retrieves a list of supported languages.
     * 
     * @return A JSON list of available languages.
     */
    @GetMapping("/getLanguages")
    public List<? extends LanguageDTO> GetLanguages() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Languages supported requested by user (`{}`)", currentUser); 
        return translationService.GetLanguages();
    }
}
