package io.github.ArtemV2007.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotBlank(message = "Имя не должно быть пустым")
        String name,

        @NotBlank(message = "Email не должен быть пустым")
        @Email(message = "Некорректный формат email")
        String email,

        @NotNull(message = "Возраст должен быть указан")
        @Min(value = 0, message = "Возраст не может быть отрицательным")
        Integer age
) {}
