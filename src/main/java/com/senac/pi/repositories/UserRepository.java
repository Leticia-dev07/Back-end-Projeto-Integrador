package com.senac.pi.repositories;

<<<<<<< HEAD
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.senac.pi.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
=======
import org.springframework.data.jpa.repository.JpaRepository;

import com.senac.pi.entities.User;

public interface UserRepository extends JpaRepository<User, Long >{

}
>>>>>>> 605a1f1f0e30830dd253152ec3f1ec4a130018bc
