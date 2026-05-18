package fr.julien.charcuterieorders.service;

import fr.julien.charcuterieorders.model.Order;
import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(User user) {

        Order order = new Order();

        LocalDateTime now = LocalDateTime.now();

        order.setUser(user);
        order.setDate(now);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    public void delete(Long Id) {

        orderRepository.deleteById(Id);

    }
}
