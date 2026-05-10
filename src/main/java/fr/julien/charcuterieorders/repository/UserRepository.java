package fr.julien.charcuterieorders.repository;

import fr.julien.charcuterieorders.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Spring génère le SQL automatiquement
}
