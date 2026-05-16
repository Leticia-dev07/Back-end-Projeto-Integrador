package com.senac.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senac.pi.entities.SuperAdmin;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
}