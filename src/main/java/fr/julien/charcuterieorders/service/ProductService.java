package fr.julien.charcuterieorders.service;

import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ProductService {
    private final ProductRepository productRepository;
    public static final List<String> CATEGORY_ORDER = List.of("boeuf", "veau", "porc");
    // Retourne tous les produits de la base
    public List<Product> getAllProducts() {
        return productRepository.findAll().stream()
                .sorted(Comparator.comparingInt((Product p) -> CATEGORY_ORDER.indexOf(p.getCategory()))
                        .thenComparing(Product::getName))
                .toList();
    }

    // Groupe une liste de produits par catégorie
    public Map<String, List<Product>> groupByCategory(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        () -> new TreeMap<>(Comparator.comparingInt(CATEGORY_ORDER::indexOf)),
                        Collectors.toList()
                ));
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }

}
