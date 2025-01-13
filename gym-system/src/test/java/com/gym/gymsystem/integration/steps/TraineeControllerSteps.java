package com.gym.gymsystem.integration.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.gymsystem.dto.trainee.TraineeRegistrationRequest;
import com.gym.gymsystem.dto.trainee.UpdateTraineeProfileRequest;
import com.gym.gymsystem.dto.trainer.TrainerRegistrationRequest;
import com.gym.gymsystem.dto.trainer.UpdateTrainerProfileRequest;
import com.gym.gymsystem.dto.training.AddTrainingRequest;
import com.gym.gymsystem.dto.training.UpdateTraineeTrainersRequest;
import com.gym.gymsystem.dto.user.UpdateStatusRequest;
import com.gym.gymsystem.service.AuthorizationService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.PostConstruct;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class TraineeControllerSteps {

    @Autowired
    private WebApplicationContext context;
    @Mock
    private AuthorizationService authorizationService;
    private MockMvc mockMvc;
    private ResponseEntity<?> response;
    private TraineeRegistrationRequest request;
    private TrainerRegistrationRequest trainerRegistrationRequest;
    private String firstName;
    private String lastName;
    private String traineeUsername;
    private String trainerUsername;

    @PostConstruct
    public void initialize() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    private String asJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Given("I have a valid JWT token for {string}")
    public void the_user_is_authenticated_as(String arg) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("ROLE_ADMIN".equals(arg)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            when(authorizationService.isAdmin()).thenReturn(true);
            when(authorizationService.isTrainer()).thenReturn(false);
        } else if ("ROLE_TRAINEE".equals(arg)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TRAINEE"));
            when(authorizationService.isTrainer()).thenReturn(false);
            when(authorizationService.isAdmin()).thenReturn(false);
        }
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("trainer1", null, authorities)
        );
        when(authorizationService.isAuthenticatedUser("trainer1")).thenReturn(true);
    }

    @When("I send a POST request to {string} with valid registration details")
    public void iSendAPOSTRequestToWithValidRegistrationDetails(String arg) throws Exception {
        firstName = UUID.randomUUID().toString();
        lastName = UUID.randomUUID().toString();
        traineeUsername = firstName+"."+lastName;

        request = TraineeRegistrationRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address("address")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/trainee")
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andReturn();
        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @Then("I should receive a {int} status code")
    public void iShouldReceiveAStatusCode(int arg0) {
        System.out.println(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.valueOf(arg0));
    }

    @And("the response should contain the registered username and password")
    public void theResponseShouldContainTheRegisteredUsernameAndPassword() {
        assertThat(response.getBody()).asString().contains("username");
        assertThat(response.getBody()).asString().contains("password");
    }

    @When("I send a POST request to {string} with invalid registration details")
    public void iSendAPOSTRequestToWithInvalidRegistrationDetails(String arg0) throws Exception {
        request = TraineeRegistrationRequest.builder()
                .firstName("trainer1")
                .address("address")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/trainee")
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andReturn();
        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @And("the response should indicate the missing fields")
    public void theResponseShouldIndicateTheMissingFields() {
        assertThat(response.getBody()).asString().contains("lastName\":\"lastName is required");
    }

    @When("I send a GET request")
    public void iSendAGETRequestTo() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/trainee/" + firstName + "." + lastName)
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString("")))
                .andReturn();

        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @And("the response should contain the trainee's profile information")
    public void theResponseShouldContainTheTraineeSProfileInformation() {
        assertThat(response.getBody()).asString().contains("username");
        assertThat(response.getBody()).asString().contains("firstName");
        assertThat(response.getBody()).asString().contains("lastName");
    }

    @And("the response should indicate access is denied")
    public void theResponseShouldIndicateAccessIsDenied() {
        assertThat(response.getBody()).asString().contains("message\":\"You do not have permission");
    }

    @When("I send a PUT request with valid update details")
    public void iSendAPUTRequestToWithValidUpdateDetails() throws Exception {
        String username = firstName + "." + lastName;
        firstName = UUID.randomUUID().toString();
        lastName = UUID.randomUUID().toString();

        UpdateTraineeProfileRequest request = UpdateTraineeProfileRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address("address")
                .isActive(true)
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/trainee/" + username)
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andReturn();

        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @And("the response should indicate the profile was updated")
    public void theResponseShouldIndicateTheProfileWasUpdated() {
        assertThat(response.getBody()).asString().contains(firstName);
        assertThat(response.getBody()).asString().contains(lastName);
    }

    @When("I send a DELETE request")
    public void iSendADELETERequestTo() throws Exception {
        String username = firstName + "." + lastName;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/trainee/" + username)
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString("")))
                .andReturn();

        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @And("the response should indicate the trainee was deleted")
    public void theResponseShouldIndicateTheTraineeWasDeleted() {
        assertThat(response.getBody()).asString().contains("{\"message\":\"The Trainee has been deleted\"}");
    }

    @Given("I send a POST request")
    public void iSendAPOSTRequest() throws Exception {
        firstName = UUID.randomUUID().toString();
        lastName = UUID.randomUUID().toString();
        request = TraineeRegistrationRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address("address")
                .build();

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/trainee")
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andReturn();

        System.out.println(new ResponseEntity<>(response.getResponse().getContentAsString(),
                HttpStatus.valueOf(response.getResponse().getStatus())).getBody());
    }

    @When("I send a POST request to trainer with valid registration details")
    public void iSendAPOSTRequestToTrainerWithValidRegistrationDetails() throws Exception {
        firstName = UUID.randomUUID().toString();
        lastName = UUID.randomUUID().toString();
        trainerUsername = firstName+"."+lastName;

        trainerRegistrationRequest = TrainerRegistrationRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialization("Nutrition")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/trainer")
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(trainerRegistrationRequest)))
                .andReturn();
        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @When("I send a POST request to trainer with missing required fields")
    public void iSendAPOSTRequestToTrainerWithMissingRequiredFields() throws Exception {
        trainerRegistrationRequest = TrainerRegistrationRequest.builder()
                .firstName("trainer1")
                .specialization("Nutrition")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/trainer")
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(trainerRegistrationRequest)))
                .andReturn();
        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @When("I send a GET request to {string} with an existing trainer's username")
    public void iSendAGETRequestToWithAnExistingTrainerSUsername(String arg0) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/trainer/" + firstName + "." + lastName)
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString("")))
                .andReturn();
        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @And("the response should contain the trainer's profile details")
    public void theResponseShouldContainTheTrainerSProfileDetails() {
        assertThat(response.getBody()).asString().contains(firstName);
        assertThat(response.getBody()).asString().contains(lastName);
    }

    @Given("I have a valid JWT token for a non-admin user")
    public void iHaveAValidJWTTokenForANonAdminUser() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_TRAINER"));
        when(authorizationService.isTrainer()).thenReturn(true);
        when(authorizationService.isAdmin()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("trainer1", null, authorities)
        );
        when(authorizationService.isAuthenticatedUser("trainer1")).thenReturn(true);
    }

    @When("I send a PUT request to {string} with valid update details")
    public void iSendAPUTRequestToWithValidUpdateDetails(String arg0) throws Exception {
        String username = firstName + "." + lastName;
        firstName = UUID.randomUUID().toString();
        lastName = UUID.randomUUID().toString();

        UpdateTrainerProfileRequest request = UpdateTrainerProfileRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .specialization("Nutrition")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/trainer/" + username)
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andReturn();

        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @When("I send a PATCH request to {string} with {string} set to true")
    public void iSendAPATCHRequestToWithSetToTrue(String arg0, String arg1) throws Exception {
        String username = firstName + "." + lastName;
        firstName = UUID.randomUUID().toString();
        lastName = UUID.randomUUID().toString();

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setIsActive(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/trainer/status/" + username)
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andReturn();

        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @And("the response should indicate the trainer has been activated successfully")
    public void theResponseShouldIndicateTheTrainerHasBeenActivatedSuccessfully() {
        assertThat(response.getBody()).asString().contains("has been activated successfully");
    }

    @And("the response should indicate the trainer was not found")
    public void theResponseShouldIndicateTheTrainerWasNotFound() {
        assertThat(response.getBody()).asString().contains("Trainer not found");
    }

    @When("I request the list of active trainers not assigned to trainee {string}")
    public void iRequestTheListOfActiveTrainersNotAssignedToTrainee(String arg0) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/training/trainers/not-assigned")
                        .header("X-Transaction-Id", "12345")
                        .param("findUsername",traineeUsername)
                        .contentType("application/json")
                        .content(asJsonString("")))
                .andReturn();

        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @When("I send a request to add the new training")
    public void iSendARequestToAddTheNewTraining() throws Exception {
        AddTrainingRequest request = AddTrainingRequest.builder()
                .traineeUsername(traineeUsername)
                .trainerUsername(trainerUsername)
                .trainingName("Nutrition")
                .trainingType("Nutrition")
                .trainingDuration(45L)
                .trainingDate("2026-01-10")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/training")
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andReturn();

        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @Then("I should receive a success message {string}")
    public void iShouldReceiveASuccessMessage(String arg0) {
        assertThat(response.getBody()).asString().contains("Training added successfully");
    }

    @Then("I should receive a list of trainers not assigned to the trainee")
    public void iShouldReceiveAListOfTrainersNotAssignedToTheTrainee() {
        assertThat(response.getBody()).asString().contains("{\"username\":\"john.doe\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"specialization\":\"Strength Training\"}");
    }

    @Given("I have a valid JWT token for a trainee user")
    public void iHaveAValidJWTTokenForATraineeUser() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_TRAINEE"));
        when(authorizationService.isTrainer()).thenReturn(false);
        when(authorizationService.isAdmin()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("trainer1", null, authorities)
        );
        when(authorizationService.isAuthenticatedUser("trainer1")).thenReturn(true);
    }

    @When("I send a request to update trainers for trainee")
    public void iSendARequestToUpdateTrainersForTrainee() throws Exception {
        List<String> trainersUsernames = new ArrayList<>();
        trainersUsernames.add(trainerUsername);
        UpdateTraineeTrainersRequest request = UpdateTraineeTrainersRequest.builder()
                .trainersUsernames(trainersUsernames).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/training/trainee/"+traineeUsername)
                        .header("X-Transaction-Id", "12345")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andReturn();

        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @Then("the response should contain the updated list of trainers assigned to the trainee")
    public void theResponseShouldContainTheUpdatedListOfTrainersAssignedToTheTrainee() {
        assertThat(response.getBody()).asString().contains(trainerUsername);
    }

    @And("I request the list of trainings for trainee from {string} to {string}")
    public void iRequestTheListOfTrainingsForTraineeFromTo(String arg0, String arg1) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/training/trainee/"+traineeUsername)
                        .header("X-Transaction-Id", "12345")
                        .param("periodFrom",arg0)
                        .param("periodTo",arg1)
                        .contentType("application/json")
                        .content(asJsonString("")))
                .andReturn();

        response = new ResponseEntity<>(result.getResponse().getContentAsString(),
                HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @Then("I should receive the list of trainings matching the criteria")
    public void iShouldReceiveTheListOfTrainingsMatchingTheCriteria() {
        assertThat(response.getBody()).asString().contains(trainerUsername);
    }
}
