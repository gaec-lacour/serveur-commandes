package fr.julien.charcuterieorders.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderForm {

    private List<OrderLineForm> items;
}