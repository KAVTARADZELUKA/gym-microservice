package com.example.trainerworkloadservice.controller;

import com.example.trainerworkloadservice.dto.TrainerMonthlySummaryResponse;
import com.example.trainerworkloadservice.dto.WorkloadRequest;
import com.example.trainerworkloadservice.exception.CustomAccessDeniedException;
import com.example.trainerworkloadservice.service.AuthorizationService;
import com.example.trainerworkloadservice.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workloads")
public class TrainerWorkloadController {
    private final TrainerWorkloadService service;
    private final AuthorizationService authorizationService;

    public TrainerWorkloadController(TrainerWorkloadService service, AuthorizationService authorizationService) {
        this.service = service;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public HttpStatus updateWorkload(@Valid @RequestBody WorkloadRequest request,@RequestHeader("X-Transaction-Id") String transactionId) {
        if (!authorizationService.isAdmin() && !authorizationService.isAuthenticatedUser(request.getUsername())) {
            throw new CustomAccessDeniedException("You do not have permission to update this trainer workload");
        }
        service.updateWorkload(transactionId,request);
        return HttpStatus.OK;
    }

    @GetMapping("/{username}/summary")
    public TrainerMonthlySummaryResponse getTrainerMonthlySummary(@PathVariable String username) {
        return service.getTrainerMonthlySummary(username);
    }
}
