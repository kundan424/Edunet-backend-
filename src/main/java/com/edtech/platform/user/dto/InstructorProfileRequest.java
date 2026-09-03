package com.edtech.platform.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorProfileRequest {

    @NotBlank(message = "Bio cannot be blank")
    private String bio;

    @NotBlank(message = "Expertise cannot be blank")
    private String expertise;
}
