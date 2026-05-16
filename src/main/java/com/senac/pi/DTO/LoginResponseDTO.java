package com.senac.pi.DTO;

/**
 * DTO que envia o Token e a Role do usuário para o Frontend.
 */
public record LoginResponseDTO(String token, String role) {
}