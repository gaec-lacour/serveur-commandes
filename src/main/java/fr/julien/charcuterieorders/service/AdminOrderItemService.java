package fr.julien.charcuterieorders.service;

import fr.julien.charcuterieorders.model.*;
import fr.julien.charcuterieorders.repository.AdminOrderItemRepository;
import fr.julien.charcuterieorders.repository.ProductRepository;
import fr.julien.charcuterieorders.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderItemService {

    private final AdminOrderItemRepository adminOrderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<AdminOrderItem> getAll() {
        return adminOrderItemRepository.findAll();
    }

    public void saveOrUpdate(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        OrderItemId id = new OrderItemId(userId, productId);

        if (quantity == 0) {
            adminOrderItemRepository.deleteById(id);
            return;
        }

        AdminOrderItem item = adminOrderItemRepository
                .findById(id)
                .orElse(new AdminOrderItem(id, user, product, 0));
        item.setQuantity(quantity);
        adminOrderItemRepository.save(item);
    }
    public void syncFromOrderItems(List<OrderItem> orderItems) {
        adminOrderItemRepository.deleteAll();
        for (OrderItem item : orderItems) {
            OrderItemId id = new OrderItemId(item.getUser().getId(), item.getProduct().getId());
            AdminOrderItem adminItem = new AdminOrderItem(id, item.getUser(), item.getProduct(), item.getQuantity());
            adminOrderItemRepository.save(adminItem);
        }
    }
    public void resetAll() {
        adminOrderItemRepository.deleteAll();
    }
}