package com.universalis.blog.domain.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}