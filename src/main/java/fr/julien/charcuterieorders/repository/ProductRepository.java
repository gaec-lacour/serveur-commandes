package fr.julien.charcuterieorders.repository;

import fr.julien.charcuterieorders.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue(); // ≈ Product::where('active', true)->get()
    List<Product> findByCategory(String category);
}
