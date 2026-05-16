package fr.julien.charcuterieorders.service;

import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.OrderItemId;
import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.repository.OrderItemRepository;
import fr.julien.charcuterieorders.repository.ProductRepository;
import fr.julien.charcuterieorders.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public void saveOrUpdate(Long userId, Long productId, Integer quantity) {

        OrderItemId id = new OrderItemId(userId, productId);

        OrderItem item = orderItemRepository.findById(id)
                .orElse(null);

        if (quantity == null || quantity <= 0) {
            if (item != null) {
                orderItemRepository.delete(item);
            }
            return;
        }

        if (item == null) {
            item = new OrderItem();
            item.setId(id);
            item.setUser(userRepository.getReferenceById(userId));
            item.setProduct(productRepository.getReferenceById(productId));
        }

        item.setQuantity(quantity);

        orderItemRepository.save(item);
    }
    public List<OrderItem> getAll() {
            return orderItemRepository.findAll();
        }
}
