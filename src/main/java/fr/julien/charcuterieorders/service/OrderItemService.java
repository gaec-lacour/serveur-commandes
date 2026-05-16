package fr.julien.charcuterieorders.service;

import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.OrderItemId;
import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> getByUser(User user) {
        return orderItemRepository.findByUser(user);
    }

    // ≈ updateOrInsert en Laravel
    public void saveOrUpdate(User user, Product product, Integer quantity) {
        OrderItemId id = new OrderItemId(user.getId(), product.getId());

        OrderItem item = orderItemRepository.findById(id)
                .orElse(new OrderItem(id, user, product, null));

        if (quantity == null || quantity == 0) {
            orderItemRepository.delete(item);  // quantité vide = suppression
        } else {
            item.setQuantity(quantity);
            orderItemRepository.save(item);
        }
    }

    // Toutes les commandes pour la vue admin
    public List<OrderItem> getAll() {
        return orderItemRepository.findAll();
    }

}

