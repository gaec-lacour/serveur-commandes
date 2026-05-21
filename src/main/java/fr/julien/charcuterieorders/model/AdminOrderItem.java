package fr.julien.charcuterieorders.model;

import jakarta.persistence.*;
import lombok.*;

// model/AdminOrderItem.java
@Entity
@Table(name = "admin_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderItem {

    @EmbeddedId
    private OrderItemId id; // tu réutilises le même embeddable

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @Column(name = "done_quantity", nullable = false)
    private int doneQuantity;
}
