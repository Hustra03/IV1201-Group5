package com.example.demo.presentation.integration;

import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.example.demo.service.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
// Based on the guide https://spring.io/guides/gs/testing-web
// This is used to specify the property file :
// https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-testpropertysource.html
@ActiveProfiles("test") // This is done for the purpose of loading the application-test properties file
@SpringBootTest(properties = { "spring.profiles.active=test" })
/**
 * This class performs mock mvc testing of PersonController
 */
public class PersonControllerMockMVCTest {

    @Spy
    private JwtService jwtService;

    // This is used to test endpoint controllers without running the full server
    @Autowired
    private MockMvc mockMvc;

    /**
     * This tests that a request without a parameter will return a response with bad
     * request http code with the correct error message. Note that it uses jwtService to create a fake but valid token.
     * 
     * @throws Exception This throws any exception which occurs
     */
    @Test
    void findPersonNoParameterEndpointTest() throws Exception {
        this.mockMvc.perform(get("/person/findPerson").header("Authorization", "Bearer "+jwtService.generateToken("JoelleWilkinson"))).andDo(print()).andExpect(content().string("Please provide PNR, email, or username for search.")).andExpect(status().isBadRequest());
    }

    /**
     * This tests that a request which will return a response with ok
     * http status code with the correct person based on the stored values with prn
     * as a parameter (this assumes the existing database is used, if not it may be incorrect)
     * 
     * @throws Exception
     */
    @Test
    void findPersonPnrEndpointTest() throws Exception {
        this.mockMvc.perform(get("/person/findPerson?pnr=20070114-1252").header("Authorization", "Bearer "+jwtService.generateToken("JoelleWilkinson"))).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("{\"id\":11,\"name\":\"Leroy\",\"surname\":\"Crane\",\"pnr\":\"20070114-1252\",\"email\":\"l_crane118@finnsinte.se\",\"role\":{\"roleId\":2,\"name\":\"applicant\"},\"username\":null}"));
    }

    /**
     * This tests that a request which will return a response with ok
     * http status code with the correct person based on the stored values with prn
     * as a parameter (this assumes the existing database is used, if not it may be incorrect)
     * 
     * @throws Exception
     */
    @Test
    void findPersonEmailEndpointTest() throws Exception {
        // We first set up the mock implementation of the function
        this.mockMvc.perform(get("/person/findPerson?email=l_crane118@finnsinte.se").header("Authorization", "Bearer "+jwtService.generateToken("JoelleWilkinson"))).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string("{\"id\":11,\"name\":\"Leroy\",\"surname\":\"Crane\",\"pnr\":\"20070114-1252\",\"email\":\"l_crane118@finnsinte.se\",\"role\":{\"roleId\":2,\"name\":\"applicant\"},\"username\":null}"));
    }

    /**
     * This tests that a request which will return a response with ok
     * http status code with the correct person based on the stored values with prn
     * as a parameter (this assumes the existing database is used, if not it may be incorrect)
     * 
     * @throws Exception
     */
    @Test    
    void findPersonUsernameEndpointTest() throws Exception {

        this.mockMvc.perform(get("/person/findPerson?username=JoelleWilkinson").header("Authorization", "Bearer "+jwtService.generateToken("JoelleWilkinson"))).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"name\":\"Joelle\",\"surname\":\"Wilkinson\",\"pnr\":null,\"email\":null,\"role\":{\"roleId\":1,\"name\":\"recruiter\"},\"username\":\"JoelleWilkinson\"}"));

    }

    /**
     * This tests that a request which will return a response with 404
     * http status code with the correct error message
     * @throws Exception
     */
    @Test
    void findPersonUsernameMissingUsernameEndpointTest() throws Exception {

        this.mockMvc.perform(get("/person/findPerson?username=notARealPerson").header("Authorization", "Bearer "+jwtService.generateToken("JoelleWilkinson"))).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Person not found."));

    }

}
