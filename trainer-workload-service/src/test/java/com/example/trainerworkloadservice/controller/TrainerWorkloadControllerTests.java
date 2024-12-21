package com.example.trainerworkloadservice.controller;

import com.example.trainerworkloadservice.config.GlobalExceptionHandler;
import com.example.trainerworkloadservice.dto.TrainerMonthlySummaryResponse;
import com.example.trainerworkloadservice.dto.WorkloadRequest;
import com.example.trainerworkloadservice.exception.CustomAccessDeniedException;
import com.example.trainerworkloadservice.service.AuthorizationService;
import com.example.trainerworkloadservice.service.TrainerWorkloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.example.trainerworkloadservice.dto.WorkloadEnum.ADD;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadControllerTests {

    private MockMvc mockMvc;

    @Mock
    private TrainerWorkloadService service;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private TrainerWorkloadController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void updateWorkload_ShouldReturnOk_WhenAuthorized() throws Exception {
        WorkloadRequest request = WorkloadRequest.builder()
                .username("testUsername")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate("2021-08-01")
                .duration(60L)
                .actionType(ADD.getType())
                .build();

        when(authorizationService.isAdmin()).thenReturn(true);

        mockMvc.perform(post("/api/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Transaction-Id", "123")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).updateWorkload("123", request);
    }

    @Test
    void updateWorkload_ShouldThrowAccessDenied_WhenUnauthorized() throws Exception {
        WorkloadRequest request = WorkloadRequest.builder()
                .username("testUsername")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate("2021-08-01")
                .duration(60L)
                .actionType(ADD.getType())
                .build();


        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.isAuthenticatedUser(request.getUsername())).thenReturn(false);

        mockMvc.perform(post("/api/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Transaction-Id", "123")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("You do not have permission to update this trainer workload"));

        verify(service, never()).updateWorkload(any(), any());
    }


    @Test
    void getTrainerMonthlySummary_ShouldReturnSummary_WhenCalled() throws Exception {
        String username = "testUsername";
        TrainerMonthlySummaryResponse expectedResponse = TrainerMonthlySummaryResponse.builder()
                .username(username)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .years(List.of())
                .build();

        when(service.getTrainerMonthlySummary(username)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/workloads/{username}/summary", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(expectedResponse.getUsername()))
                .andExpect(jsonPath("$.firstName").value(expectedResponse.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(expectedResponse.getLastName()))
                .andExpect(jsonPath("$.isActive").value(expectedResponse.getIsActive()));

        verify(service).getTrainerMonthlySummary(username);
    }

}