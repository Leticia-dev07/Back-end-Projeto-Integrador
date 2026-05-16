package com.senac.pi.DTO;

import com.senac.pi.entities.Coordenador;

public record CoordenadorDTO(
    Long id,
    String name,
    String email,
    String password 
) {
    public CoordenadorDTO(Coordenador entity) {
        this(entity.getId(), entity.getName(), entity.getEmail(), null);
    }
}