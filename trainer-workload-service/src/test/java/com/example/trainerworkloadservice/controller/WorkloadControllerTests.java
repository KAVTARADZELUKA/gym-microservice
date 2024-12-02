package com.example.trainerworkloadservice.controller;

import com.example.trainerworkloadservice.dto.TrainerMonthlySummaryResponse;
import com.example.trainerworkloadservice.dto.WorkloadRequest;
import com.example.trainerworkloadservice.exception.CustomAccessDeniedException;
import com.example.trainerworkloadservice.service.AuthorizationService;
import com.example.trainerworkloadservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkloadControllerTests {
    @InjectMocks
    private TrainerWorkloadController controller;

    @Mock
    private TrainerWorkloadService service;

    @Mock
    private AuthorizationService authorizationService;

    private WorkloadRequest validRequest;

    @BeforeEach
    public void setup() {
        validRequest = new WorkloadRequest();
        validRequest.setUsername("testUsername");
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setIsActive(true);
        validRequest.setTrainingDate("2021-08-01");
        validRequest.setDuration(60L);
        validRequest.setActionType("ADD");
    }

    @Test
    public void updateWorkload_ShouldReturnOK_WhenAuthorized() {
        when(authorizationService.isAdmin()).thenReturn(true);

        HttpStatus status = controller.updateWorkload(validRequest, "123");
        verify(service).updateWorkload("123", validRequest);
        assertEquals(HttpStatus.OK, status);
    }

    @Test
    public void updateWorkload_ShouldThrowException_WhenNotAuthorized() {
        when(authorizationService.isAdmin()).thenReturn(false);
        when(authorizationService.isAuthenticatedUser(validRequest.getUsername())).thenReturn(false);

        assertThrows(CustomAccessDeniedException.class,
                () -> controller.updateWorkload(validRequest, "123"));
    }

    @Test
    public void getTrainerMonthlySummary_ShouldReturnSummary_WhenCalled() {
        String username = "testUsername";
        TrainerMonthlySummaryResponse expectedResponse = new TrainerMonthlySummaryResponse();

        when(service.getTrainerMonthlySummary(username)).thenReturn(expectedResponse);

        TrainerMonthlySummaryResponse response = controller.getTrainerMonthlySummary(username);
        assertEquals(expectedResponse, response);
        verify(service).getTrainerMonthlySummary(username);
    }
}
