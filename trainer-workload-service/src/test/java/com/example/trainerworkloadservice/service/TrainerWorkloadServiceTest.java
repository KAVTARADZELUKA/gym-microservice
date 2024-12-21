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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.example.trainerworkloadservice.dto.WorkloadEnum.ADD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
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
        validRequest = WorkloadRequest.builder()
                .username("testUsername")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate("2021-08-01")
                .duration(60L)
                .actionType(ADD.getType())
                .build();
    }

    @Test
    void updateWorkloadShouldThrowIfUserIsInactiveAndAdding() {
        validRequest.setIsActive(false);

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateWorkload("123", validRequest);
        });

        verify(repository, never()).save(any(TrainerWorkload.class));
    }

    @Test
    void getTrainerMonthlySummaryShouldReturnCorrectData() {
        TrainerWorkload mockedWorkload = createMockWorkload();
        when(repository.findByUsername(any())).thenReturn(Optional.of(mockedWorkload));

        TrainerMonthlySummaryResponse summaryResponse = service.getTrainerMonthlySummary("testUsername");

        assertNotNull(summaryResponse);
        assertEquals("testUsername", summaryResponse.getUsername());
        assertNotNull(summaryResponse.getYears());

        verify(repository, times(1)).findByUsername(eq("testUsername"));
    }

    @Test
    void getTrainerMonthlySummaryShouldThrowIfNoTrainerFound() {
        when(repository.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            service.getTrainerMonthlySummary("wrongUsername");
        });

        verify(repository, times(1)).findByUsername(eq("wrongUsername"));
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