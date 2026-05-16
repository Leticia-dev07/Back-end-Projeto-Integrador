package com.senac.pi.DTO;

import com.senac.pi.entities.User;
import com.senac.pi.entities.enums.UserRole;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;

    public UserDTO() {}

    public UserDTO(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.role = entity.getRole();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
}