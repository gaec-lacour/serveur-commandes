package fr.julien.charcuterieorders.repository;

import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.OrderItemId;
import fr.julien.charcuterieorders.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository <OrderItem, OrderItemId> {
    List<OrderItem> findByUser(User user);
    void deleteByUser(User user);
}
