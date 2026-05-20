package fr.julien.charcuterieorders.service;

import fr.julien.charcuterieorders.model.*;
import fr.julien.charcuterieorders.repository.AdminOrderItemRepository;
import fr.julien.charcuterieorders.repository.ProductRepository;
import fr.julien.charcuterieorders.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderItemService {

    private final AdminOrderItemRepository adminOrderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<AdminOrderItem> getAll() {
        return adminOrderItemRepository.findAll();
    }

    public void saveOrUpdate(Long userId, Long productId, Integer quantity, Integer doneQuantity) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        OrderItemId id = new OrderItemId(userId, productId);

        if (quantity == 0) {
            adminOrderItemRepository.deleteById(id);
            return;
        }

        AdminOrderItem item = adminOrderItemRepository
                .findById(id)
                .orElse(new AdminOrderItem(id, user, product, 0, 0));
        item.setQuantity(quantity);
        item.setDoneQuantity(doneQuantity);
        adminOrderItemRepository.save(item);
    }
    public void syncFromOrderItems(List<OrderItem> orderItems) {

        Map<OrderItemId, AdminOrderItem> existing =
                adminOrderItemRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                AdminOrderItem::getId,
                                Function.identity()
                        ));

        List<AdminOrderItem> toSave = new ArrayList<>();

        for (OrderItem item : orderItems) {

            OrderItemId id = new OrderItemId(
                    item.getUser().getId(),
                    item.getProduct().getId()
            );

            AdminOrderItem previous = existing.get(id);

            int doneQuantity = (previous != null)
                    ? previous.getDoneQuantity()
                    : 0;

            AdminOrderItem adminItem = new AdminOrderItem(
                    id,
                    item.getUser(),
                    item.getProduct(),
                    item.getQuantity(),
                    doneQuantity
            );

            toSave.add(adminItem);
        }

        adminOrderItemRepository.saveAll(toSave);
    }
    public void resetAll() {
        adminOrderItemRepository.deleteAll();
    }
}