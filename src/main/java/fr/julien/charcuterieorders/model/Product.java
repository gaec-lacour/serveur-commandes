package fr.julien.charcuterieorders.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category; //"boeuf", "veau", "porc"

    @ManyToMany(mappedBy = "accessibleProducts")
    private List<User> users = new ArrayList<>();
}
