package naitei.group5.workingspacebooking.repository;

import naitei.group5.workingspacebooking.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import naitei.group5.workingspacebooking.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);
    
    Optional<User> findById(Integer id);
}
