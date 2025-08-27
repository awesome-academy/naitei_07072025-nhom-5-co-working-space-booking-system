package naitei.group5.workingspacebooking.repository;

import naitei.group5.workingspacebooking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
