package fr.julien.charcuterieorders.repository;

import fr.julien.charcuterieorders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
