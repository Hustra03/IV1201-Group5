package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.domain.dto.CompetenceDTO;
import com.example.demo.domain.dto.CompetenceTranslationDTO;
import com.example.demo.domain.entity.Competence;
import com.example.demo.domain.entity.CompetenceTranslation;
import com.example.demo.domain.entity.Language;
import com.example.demo.presentation.restException.LanguageNotFoundException;
import com.example.demo.presentation.restException.SpecificCompetenceNotFoundException;
import com.example.demo.presentation.restException.TranslationsNotFoundException;
import com.example.demo.repository.CompetenceRepository;
import com.example.demo.repository.CompetenceTranslationRepository;
import com.example.demo.repository.LanguageRepository;

@Service
//This service is resonsible for handling translation related logic
public class TranslationService {
    private final CompetenceRepository competenceRepository;
    private final LanguageRepository languageRepository;
    private final CompetenceTranslationRepository competenceTranslationRepository;

    /**
     * Constructs a new instance of the TranslationService (Spring boot managed).
     *
     * @param competenceRepository the repository for accessing competence database data
     */
    public TranslationService(CompetenceRepository competenceRepository,LanguageRepository languageRepository,CompetenceTranslationRepository competenceTranslationRepository) {
        this.competenceRepository = competenceRepository;
        this.languageRepository=languageRepository;
        this.competenceTranslationRepository=competenceTranslationRepository;
    }

    /**
     * Returns a list of all the existing competences, may contain 0 or more elements
     * @return the list containing the existing competences
     * 
     */
    public List<? extends CompetenceDTO> GetCompetences()
    {
        return competenceRepository.findAll();
    }

    /**
     * Returns a specific competence based on the id parameter, and throws a SpecificCompetenceNotFound error if it does not exist, which is handeled by TranslationRestAdvice.java
     * @throws SpecificCompetenceNotFound if no competence exists with the specified id, and results in TranslationRestAdvice.java sending a 404 with a relevant error message
     * @param id the id which is searched for
     * @return If it does not throw the above exception, it will return a DTO representing the specified competence
     * 
     */
    public CompetenceDTO GetSpecificCompetence(Integer id)
    {
        //We search for a competence with this specific id
        Optional<Competence> competenceContainer=competenceRepository.findById(id);

        //We then confirm that the competence we searched for did exist, and if not we throw a specific error
        if (competenceContainer.isPresent() ==false ) {
            throw new SpecificCompetenceNotFoundException(id);
        }

        return competenceContainer.get();
    }

    /**
     * Returns a specific competence translations based on the languageName parameter
     * @throws LanguageNotFoundException if no language exists with the specified name, and results in TranslationRestAdvice.java sending a 404 with a relevant error message
     * @throws TranslationsNotFoundException if no competence translations exists for the specified language, and results in TranslationRestAdvice.java sending a 404 with a relevant error message
     * @param languageName the name of the language for which translations are searched for
     * @return If it does not throw one of the above exception, it will return a list representing competence translations for the specified language
     */

    public List<? extends CompetenceTranslationDTO> GetCompetenceTranslation(String languageName)
    {
        
        Language langague;
        //We try to retrive the specific language, and if it can not be found we throw a specific error
        try {
            
            langague=languageRepository.findByName(languageName);
            if (langague==null) {
                throw new LanguageNotFoundException(languageName);
            }
        } catch (Exception e) {
            throw new LanguageNotFoundException(languageName);
        }
        //Then, if the language does exist, we search for translations in that language (with the matching language id), and we will throw TranslationsNotFoundException if no such translations exist 
        List<CompetenceTranslation> translations=null;
        try {
           translations=competenceTranslationRepository.findByLanguage_id(langague.getLanguageId());
           if (translations==null||translations.size()==0) {
            throw new TranslationsNotFoundException(languageName);
            }
        } catch (Exception e) {
            throw new TranslationsNotFoundException(languageName);
        }

        return translations;

    }
}
