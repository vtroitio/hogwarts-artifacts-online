package com.valik.hogwarts_artifacts_online.hogwartsUser.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserDto(
    
    Integer id,

    @NotEmpty(message = "username is required.")
    String username,

    boolean enabled,

    @NotEmpty(message = "roles is required.")
    String roles

) {

}
