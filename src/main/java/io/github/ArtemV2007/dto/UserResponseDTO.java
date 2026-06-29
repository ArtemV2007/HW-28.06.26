package io.github.ArtemV2007.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {}
