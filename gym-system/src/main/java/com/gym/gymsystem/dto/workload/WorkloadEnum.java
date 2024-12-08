package com.gym.gymsystem.dto.workload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkloadEnum {
    ADD("ADD"),
    DELETE("DELETE");

    private final String type;
}
