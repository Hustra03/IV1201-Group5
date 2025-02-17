package com.example.demo.presentation;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@AutoConfigureMockMvc
//Based on the guide https://spring.io/guides/gs/testing-web 
//This is used to specify the property file : https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-testpropertysource.html
@ActiveProfiles("test") //This is done for the purpose of loading the application-test properties file
@SpringBootTest(properties = {"spring.profiles.active=test"}) 
public class ApiEndpointControllerExampleTest {
    @Autowired
    //This is used to test endpoint controllers without running the full server
	private MockMvc mockMvc;

    @Test
    void basicAnswerEndpointTest() throws Exception 
    {
        this.mockMvc.perform(get("/test/")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("test123")));
    }

    @Test
    void basicServiceAnswerEndpointTest() throws Exception 
    {
        this.mockMvc.perform(get("/test/service")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("basic service communication")));
    }

    @Test
    void basicDatabaseEndpointIncorrectPathTest() throws Exception 
    {
        this.mockMvc.perform(get("/test/database?1")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void basicDatabaseEndpointIncorrectParameterValueTest() throws Exception 
    {
        this.mockMvc.perform(get("/test/database?person_id=fail")).andDo(print()).andExpect(status().is4xxClientError()).andExpect(content().string(containsString("input was not a valid integer")));;
    }

    @Test
    void basicDatabaseEndpointCorrectParameterValueExistingPersonTest() throws Exception 
    {
        this.mockMvc.perform(get("/test/database?person_id=2")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("Person with person_id 2 did exist. Name: Martin")));;
    }

    @Test
    void basicDatabaseEndpointCorrectParameterValueNonExistingPersonTest() throws Exception 
    {
        this.mockMvc.perform(get("/test/database?person_id=0")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("Person with person_id 0 did not exist")));;
    }
}
