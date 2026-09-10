package com.edtech.platform.user.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class InstructorVerifiedEvent {
    private final UUID instructorId;
    private final UUID profileId;
}
