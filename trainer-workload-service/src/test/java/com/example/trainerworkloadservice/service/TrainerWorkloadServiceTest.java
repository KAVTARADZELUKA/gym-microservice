package com.example.trainerworkloadservice.service;

import com.example.trainerworkloadservice.dto.TrainerMonthlySummaryResponse;
import com.example.trainerworkloadservice.dto.WorkloadRequest;
import com.example.trainerworkloadservice.h2.model.TrainerWorkload;
import com.example.trainerworkloadservice.h2.model.TrainingSummary;
import com.example.trainerworkloadservice.h2.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadServiceTest {

    @InjectMocks
    private TrainerWorkloadService service;

    @Mock
    private TrainerWorkloadRepository repository;

    private WorkloadRequest validRequest;

    @BeforeEach
    public void setup() {
        validRequest = new WorkloadRequest();
        validRequest.setUsername("testUsername");
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setIsActive(true);
        validRequest.setTrainingDate("2026-09-01");
        validRequest.setDuration(60L);
        validRequest.setActionType("ADD");
    }

    @Test
    public void updateWorkloadShouldThrowIfUserIsInactiveAndAdding() {
        validRequest.setIsActive(false);

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateWorkload("123", validRequest);
        });
    }

    @Test
    public void getTrainerMonthlySummaryShouldReturnCorrectData() {
        TrainerWorkload mockedWorkload = createMockWorkload();
        when(repository.findByUsername("testUsername")).thenReturn(Optional.of(mockedWorkload));

        TrainerMonthlySummaryResponse summaryResponse = service.getTrainerMonthlySummary("testUsername");

        assertNotNull(summaryResponse);
        assertEquals("testUsername", summaryResponse.getUsername());
        assertNotNull(summaryResponse.getYears());
    }

    @Test
    public void getTrainerMonthlySummaryShouldThrowIfNoTrainerFound() {
        when(repository.findByUsername("wrongUsername")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            service.getTrainerMonthlySummary("wrongUsername");
        });
    }

    private TrainerWorkload createMockWorkload() {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setUsername("testUsername");
        workload.setFirstName("John");
        workload.setLastName("Doe");
        workload.setIsActive(true);
        TrainingSummary summary = new TrainingSummary(2021, 9, 60L);
        workload.setTrainingSummaries(List.of(summary));
        return workload;
    }
}