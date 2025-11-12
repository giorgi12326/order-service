package org.example.orderservice.dtos;

import org.example.orderservice.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FullUserDTO {
    private String username;
    private String password;
    private Role role;
}
