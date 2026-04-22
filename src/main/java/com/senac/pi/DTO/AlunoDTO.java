package com.senac.pi.DTO;

public record AlunoDTO(
    Long id, 
    String name, 
    String email, 
    String matricula, 
    String turma,
    String senhaHash
) {}