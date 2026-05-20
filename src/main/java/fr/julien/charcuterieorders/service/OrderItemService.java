package fr.julien.charcuterieorders.service;

import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.OrderItemId;
import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> getByUser(User user) {
        return orderItemRepository.findByUser(user);
    }

    public Integer getQuantity(User user, Product product) {

        return orderItemRepository.findByUserAndProduct(user, product)
                .map(OrderItem::getQuantity)
                .orElse(0);
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
    public void resetAll() {
        orderItemRepository.deleteAll();
    }

    // Toutes les commandes pour la vue admin
    public List<OrderItem> getAll() {
        return orderItemRepository.findAll();
    }

    public boolean isOrdered(Product product) {
        return orderItemRepository.existsByProductId(product.getId());
    }

}

