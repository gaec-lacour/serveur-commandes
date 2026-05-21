package fr.julien.charcuterieorders.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category; //"boeuf", "veau", "porc"

    @Column(nullable = false)
    private boolean active;

    @ManyToMany(mappedBy = "accessibleProducts")
    private List<User> users = new ArrayList<>();
}
