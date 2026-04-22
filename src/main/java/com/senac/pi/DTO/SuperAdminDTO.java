package com.senac.pi.DTO;

import com.senac.pi.entities.SuperAdmin;

public record SuperAdminDTO(
    Long id, 
    String name, 
    String email
) {
    // Construtor compacto para conversão de Entity para Record
    public SuperAdminDTO(SuperAdmin entity) {
        this(entity.getId(), entity.getName(), entity.getEmail());
    }
}