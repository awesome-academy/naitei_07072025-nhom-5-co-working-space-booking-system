package naitei.group5.workingspacebooking.repository;

import naitei.group5.workingspacebooking.entity.AccountRecovery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRecoveryRepository extends JpaRepository<AccountRecovery, Integer> {
    Optional<AccountRecovery> findByRecoveryToken(String token);
}
