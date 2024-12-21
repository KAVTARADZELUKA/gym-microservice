package com.gym.gymsystem.service;

import com.gym.gymsystem.dto.trainer.TrainerInfo;
import com.gym.gymsystem.entity.*;
import com.gym.gymsystem.feign.WorkloadInterface;
import com.gym.gymsystem.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserService userService;
    @Mock
    private WorkloadInterface workloadInterface;
    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private MessageProducer messageProducer;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(trainingService, "destination", "expectedDestination");
    }

    @Test
    public void testCreateTraining() {
        Trainer trainer1 = new Trainer();
        User user1 = new User();
        user1.setUsername("trainer1");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setIsActive(true);
        trainer1.setUser(user1);

        Trainer trainer2 = new Trainer();
        User user2 = new User();
        user2.setUsername("trainer2");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setIsActive(true);
        trainer2.setUser(user2);

        LinkedList<Trainer> trainers = new LinkedList<>();
        trainers.add(trainer1);
        trainers.add(trainer2);

        Training training = new Training();
        training.setName("Box");
        training.setTrainingDate(LocalDate.now().atStartOfDay().plusDays(1));

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Fitness");
        training.setTrainingType(trainingType);
        training.setTrainers(trainers);

        when(trainingTypeService.getTrainingTypeByName("Fitness")).thenReturn(trainingType);
        doNothing().when(messageProducer).sendTo(anyString(), any());

        trainingService.createTraining(training, "1");

        verify(trainingRepository, times(1)).save(training);
        verify(messageProducer, times(trainers.size())).sendTo(anyString(), any());

        assertEquals("Fitness", training.getTrainingType().getTrainingTypeName());
        assertNotNull(training.getTrainingType());
        assertEquals(2, training.getTrainers().size());
    }

    @Test
    public void testGetAllTrainings() {
        String username = "username";
        String password = "password";
        List<Training> trainings = Arrays.asList(new Training(), new Training());
        when(trainingRepository.findAll()).thenReturn(trainings);
        when(userService.usernamePasswordMatches(username, password)).thenReturn(true);

        Collection<Training> result = trainingService.getAllTrainings(username, password);

        assertEquals(2, result.size());
        verify(trainingRepository).findAll();
    }

    @Test
    public void testFindTrainingsByTraineeAndCriteria() {
        String username = "testUser";
        LocalDateTime fromDate = LocalDate.now().atStartOfDay();
        LocalDateTime toDate = LocalDate.now().atStartOfDay();
        String trainerName = "testTrainer";
        String trainingType = "personal";

        List<Training> trainings = Arrays.asList(new Training(), new Training());
        when(trainingRepository.findTrainingsByTraineeAndCriteria(username, fromDate, toDate, trainerName, trainingType))
                .thenReturn(trainings);
        List<Training> result = trainingService.findTrainingsByTraineeAndCriteria(username, fromDate, toDate, trainerName, trainingType);

        assertEquals(2, result.size());
        verify(trainingRepository).findTrainingsByTraineeAndCriteria(username, fromDate, toDate, trainerName, trainingType);
    }

    @Test
    public void testFindTrainingsByTrainerAndCriteria() {
        String username = "testUser";
        LocalDateTime fromDate = LocalDate.now().atStartOfDay();
        LocalDateTime toDate = LocalDate.now().atStartOfDay();
        String trainerName = "testTrainer";
        String trainingType = "personal";

        List<Training> trainings = Arrays.asList(new Training(), new Training());
        when(trainingRepository.findTrainingsByTrainerAndCriteria(username, fromDate, toDate, trainerName, trainingType))
                .thenReturn(trainings);

        List<Training> result = trainingService.findTrainingsByTrainerAndCriteria(username, fromDate, toDate, trainerName, trainingType);

        assertEquals(2, result.size());
        verify(trainingRepository).findTrainingsByTrainerAndCriteria(username, fromDate, toDate, trainerName, trainingType);
    }

    @Test
    public void testGetTrainersNotAssignedToTrainee() {
        String username = "testUser";
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Yoga");

        Trainer trainer1 = new Trainer();
        User user1 = new User();
        user1.setUsername("trainer1");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        trainer1.setUser(user1);
        trainer1.setSpecializations(List.of(trainingType));

        Trainer trainer2 = new Trainer();
        User user2 = new User();
        user2.setUsername("trainer2");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        trainer2.setUser(user2);
        trainer2.setSpecializations(List.of(trainingType));

        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);

        when(trainingRepository.findTrainersNotAssignedToTrainee(username)).thenReturn(trainers);

        List<TrainerInfo> result = trainingService.getTrainersNotAssignedToTrainee(username);

        assertEquals(2, result.size());

        assertEquals("trainer1", result.getFirst().getUsername());
        assertEquals("John", result.getFirst().getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("Yoga", result.get(0).getSpecialization());

        assertEquals("trainer2", result.get(1).getUsername());
        assertEquals("Jane", result.get(1).getFirstName());
        assertEquals("Smith", result.get(1).getLastName());
        assertEquals("Yoga", result.get(1).getSpecialization());

        verify(trainingRepository).findTrainersNotAssignedToTrainee(username);
    }

    @Test
    public void testUpdateTraineeTrainers_UserNotFound() {
        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        trainee.getUser().setUsername("testUser");
        trainee.getUser().setPassword("password");

        List<Long> trainerIds = Arrays.asList(1L, 2L);

        when(userService.usernamePasswordMatches(trainee.getUser().getUsername(), trainee.getUser().getPassword())).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            trainingService.updateTraineeTrainers(trainee.getUser().getUsername(), trainee.getUser().getPassword(), trainee, trainerIds);
        });
        assertEquals("Invalid credentials", thrown.getMessage());
    }

    @Test
    public void testUpdateTraineeTrainers_TrainerNotFound() {
        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        trainee.getUser().setUsername("testUser");
        trainee.getUser().setPassword("password");

        List<Long> trainerIds = Arrays.asList(1L, 2L);

        when(userService.usernamePasswordMatches(trainee.getUser().getUsername(), trainee.getUser().getPassword())).thenReturn(true);
        when(trainerService.findAllById(trainerIds)).thenReturn(new ArrayList<>());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            trainingService.updateTraineeTrainers(trainee.getUser().getUsername(), trainee.getUser().getPassword(), trainee, trainerIds);
        });
        assertEquals("One or more trainers not found", thrown.getMessage());
    }
}
