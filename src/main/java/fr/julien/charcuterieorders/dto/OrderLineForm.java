package fr.julien.charcuterieorders.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderLineForm {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer doneQuantity;


}
