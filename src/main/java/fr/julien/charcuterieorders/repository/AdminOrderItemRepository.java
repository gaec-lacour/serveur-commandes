package fr.julien.charcuterieorders.repository;

import fr.julien.charcuterieorders.model.AdminOrderItem;
import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.OrderItemId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// repository/AdminOrderItemRepository.java
public interface AdminOrderItemRepository
        extends JpaRepository<AdminOrderItem, OrderItemId> {
    List<AdminOrderItem> findByUserId(Long userId);
}