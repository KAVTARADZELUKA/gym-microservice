package com.example.trainerworkloadservice.service;

import com.example.trainerworkloadservice.dto.*;
import com.example.trainerworkloadservice.mongoDB.model.TrainerWorkload;
import com.example.trainerworkloadservice.mongoDB.model.TrainingSummary;
import com.example.trainerworkloadservice.mongoDB.repository.TrainerWorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.trainerworkloadservice.dto.WorkloadEnum.ADD;

@Service
public class TrainerWorkloadService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadService.class);
    private final TrainerWorkloadRepository repository;

    public TrainerWorkloadService(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void updateWorkload(String transactionId, WorkloadRequest request) {
        logger.info("Transaction Started: transactionId={}, method=updateWorkload, trainingDetails={}", transactionId, request);
        try {
            logger.info("Operation Started: transactionId={}, method=updateWorkload, action={}", transactionId, request.getActionType());
            LocalDate trainingDate = parseAndValidateDate(request.getTrainingDate());
            validateFutureDate(trainingDate);

            TrainerWorkload workload = repository.findByUsername(request.getUsername())
                    .orElseGet(() -> createNewWorkload(request));

            if (ADD.getType().equalsIgnoreCase(request.getActionType()) && (request.getIsActive() == null || !request.getIsActive())) {
                throw new IllegalArgumentException("Cannot add workload for an inactive user.");
            }

            updateTrainingSummaries(workload, trainingDate, request.getDuration(), request.getActionType());
            repository.save(workload);

            logger.info("Operation Completed: transactionId={}, method=updateWorkload, status=SUCCESS", transactionId);
        } catch (DateTimeParseException dtpe) {
            logger.error("Invalid date format: transactionId={}, method=updateWorkload, error={}", transactionId, dtpe.getMessage());
            throw new IllegalArgumentException("Invalid training date format. Use ISO_DATE format (yyyy-MM-dd).", dtpe);
        } catch (NoSuchElementException nsee) {
            logger.error("Trainer not found: transactionId={}, method=updateWorkload, error={}", transactionId, nsee.getMessage());
            throw nsee;
        } catch (IllegalArgumentException iae) {
            logger.error("Validation error: transactionId={}, method=updateWorkload, error={}", transactionId, iae.getMessage());
            throw iae;
        } catch (RuntimeException re) {
            logger.error("Unexpected runtime error: transactionId={}, method=updateWorkload, error={}", transactionId, re.getMessage());
            throw re;
        }
    }

    public TrainerMonthlySummaryResponse getTrainerMonthlySummary(String username) {
        logger.info("Transaction Started: method=getTrainerMonthlySummary, username={}", username);

        try {
            TrainerWorkload workload = repository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("Trainer not found for username: " + username));
            logger.info("Operation Started: method=getTrainerMonthlySummary, username={}", username);

            TrainerMonthlySummaryResponse response = mapToMonthlySummaryResponse(workload);
            logger.info("Operation Completed: method=getTrainerMonthlySummary, status=SUCCESS");
            return response;
        } catch (NoSuchElementException nsee) {
            logger.error("Trainer not found: method=getTrainerMonthlySummary, error={}", nsee.getMessage());
            throw nsee;
        } catch (RuntimeException re) {
            logger.error("Unexpected runtime error: method=getTrainerMonthlySummary, error={}", re.getMessage());
            throw re;
        }
    }

    private LocalDate parseAndValidateDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid training date format. Use ISO_DATE format (yyyy-MM-dd).");
        }
    }

    private void validateFutureDate(LocalDate trainingDate) {
        if (!trainingDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Training date must be in the future.");
        }
    }

    private TrainerWorkload createNewWorkload(WorkloadRequest request) {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setUsername(request.getUsername());
        workload.setFirstName(request.getFirstName());
        workload.setLastName(request.getLastName());
        workload.setIsActive(request.getIsActive());
        workload.setTrainingSummaries(new ArrayList<>());
        return workload;
    }

    private void updateTrainingSummaries(TrainerWorkload workload, LocalDate trainingDate, Long duration, String
            actionType) {
        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();

        List<TrainingSummary> summaries = workload.getTrainingSummaries();
        TrainingSummary existingSummary = findSummaryForDate(summaries, year, month);

        if (existingSummary != null) {
            updateSummary(existingSummary, duration, actionType);
        } else if (ADD.getType().equalsIgnoreCase(actionType)) {
            summaries.add(new TrainingSummary(year, month, duration));
        } else {
            throw new IllegalArgumentException("Cannot reduce duration for a non-existing training summary.");
        }
    }

    private TrainingSummary findSummaryForDate(List<TrainingSummary> summaries, int year, int month) {
        return summaries.stream()
                .filter(summary -> summary.getTrainingYear() == year && summary.getTrainingMonth() == month)
                .findFirst()
                .orElse(null);
    }

    private void updateSummary(TrainingSummary summary, Long duration, String actionType) {
        long newDuration = ADD.getType().equalsIgnoreCase(actionType)
                ? summary.getDuration() + duration
                : summary.getDuration() - duration;

        if (newDuration < 0) {
            throw new IllegalArgumentException("Training duration cannot be negative.");
        }
        summary.setDuration(newDuration);
    }

    private TrainerMonthlySummaryResponse mapToMonthlySummaryResponse(TrainerWorkload workload) {
        Map<Integer, Map<Integer, Long>> groupedSummary = workload.getTrainingSummaries().stream()
                .collect(Collectors.groupingBy(
                        TrainingSummary::getTrainingYear,
                        Collectors.groupingBy(
                                TrainingSummary::getTrainingMonth,
                                Collectors.summingLong(TrainingSummary::getDuration)
                        )
                ));

        List<YearSummary> yearSummaries = groupedSummary.entrySet().stream()
                .map(yearEntry -> {
                    List<MonthSummary> monthSummaries = yearEntry.getValue().entrySet().stream()
                            .map(monthEntry -> new MonthSummary(monthEntry.getKey(), monthEntry.getValue()))
                            .collect(Collectors.toList());

                    return new YearSummary(yearEntry.getKey(), monthSummaries);
                })
                .collect(Collectors.toList());

        return TrainerMonthlySummaryResponse.builder()
                .username(workload.getUsername())
                .firstName(workload.getFirstName())
                .lastName(workload.getLastName())
                .isActive(workload.getIsActive())
                .years(yearSummaries)
                .build();
    }
}
