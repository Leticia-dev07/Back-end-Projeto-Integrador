package com.senac.pi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senac.pi.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}