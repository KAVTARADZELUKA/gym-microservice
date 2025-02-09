package com.example.trainerworkloadservice.integration.steps;

import com.example.trainerworkloadservice.dto.WorkloadRequest;
import com.example.trainerworkloadservice.service.AuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.PostConstruct;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@DataMongoTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TrainerWorkloadSteps {

    @Autowired
    private WebApplicationContext context;
    @Mock
    private AuthorizationService authorizationService;
    private MockMvc mockMvc;
    private ResponseEntity<?> response;
    private WorkloadRequest request;

    @PostConstruct
    public void initialize() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @And("a workload request is provided with an invalid date {string}")
    public void aWorkloadRequestIsProvidedWithAnInvalidDate(String invalidDate) {
        request = WorkloadRequest.builder()
                .username("trainer1")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(invalidDate)
                .duration(120L)
                .actionType("ADD")
                .build();
    }

    @Given("the user is authenticated as {string}")
    public void the_user_is_authenticated_as(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("ADMIN".equals(role)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            when(authorizationService.isAdmin()).thenReturn(true);
            when(authorizationService.isTrainer()).thenReturn(false);
        } else if ("TRAINER".equals(role)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TRAINER"));
            when(authorizationService.isTrainer()).thenReturn(true);
            when(authorizationService.isAdmin()).thenReturn(false);
        }
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("trainer1", null, authorities)
        );
        when(authorizationService.isAuthenticatedUser("trainer1")).thenReturn(true);
    }

    @Given("a valid workload request is provided with action {string}")
    public void a_valid_workload_request_is_provided_with_action(String action) {
        request = WorkloadRequest.builder()
                .username("trainer2")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate("2026-01-10")
                .duration(120L)
                .actionType(action)
                .build();
    }

    @When("the workload update request is sent")
    public void the_workload_update_request_is_sent() throws Exception {
        String testJwtToken = "Bearer 2z-nLy-8";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/workloads")
                        .header("X-Transaction-Id", "12345")
                        .header("Authorization", testJwtToken)
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andReturn();
        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @Then("the response status should be {int} OK")
    public void the_response_status_should_be_ok(int status) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.valueOf(status));
    }

    @Then("an error message {string} should be returned")
    public void an_error_message_should_be_returned(String errorMessage) {
        assertThat(response.getBody()).asString().contains(errorMessage);
    }

    @Then("the response status should be {int} FORBIDDEN")
    public void theResponseStatusShouldBeFORBIDDEN(int arg0) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.valueOf(arg0));
    }

    private String asJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
