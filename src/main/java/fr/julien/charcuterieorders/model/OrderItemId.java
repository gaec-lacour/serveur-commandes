package fr.julien.charcuterieorders.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemId implements Serializable {
    private Long userId;
    private Long productId;
}