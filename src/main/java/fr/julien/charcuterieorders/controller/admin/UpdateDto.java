package fr.julien.charcuterieorders.controller.admin;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UpdateDto {

    Long userId;
    Long productId;
    Integer quantity;
}