package fr.julien.charcuterieorders.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderLineForm {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private int doneQuantity;


}
