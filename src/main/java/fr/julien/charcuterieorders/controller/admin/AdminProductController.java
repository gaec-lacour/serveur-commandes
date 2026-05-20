package fr.julien.charcuterieorders.controller.admin;

import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.service.OrderItemService;
import fr.julien.charcuterieorders.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/produits")
@RequiredArgsConstructor

public class AdminProductController {
    private final ProductService productService;
    private final OrderItemService orderItemService;

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> updateActive(@PathVariable Long id,
                                             @RequestBody Map<String, Boolean> body) {

        boolean active = body.get("active");

        Product product = productService.getById(id);

        if (orderItemService.isOrdered(product)) {
            return ResponseEntity.status(409).build();
        }

        productService.updateActive(id, active);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public String index(Model model) {
        Map<String, List<Product>> productsByCategory = productService.getAllProducts()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory));
        model.addAttribute("productsByCategory",
                productService.groupByCategory(productService.getAllProducts()));
        return "admin/produits/index";
    }
    @GetMapping("/nouveau")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        return "admin/produits/form";
    }

    @PostMapping
    public String store(@ModelAttribute Product product) {
        productService.save(product);
        return "redirect:/admin/produits";
    }

    @GetMapping("/{id}/modifier")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getById(id));
        return "admin/produits/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Product formProduct) {

        Product existing = productService.getById(id);

        existing.setName(formProduct.getName());
        existing.setCategory(formProduct.getCategory());
        // NE PAS toucher à active

        productService.save(existing);

        return "redirect:/admin/produits";
    }

    @PostMapping("/{id}/supprimer")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Impossible de supprimer ce produit : il est encore associé à des clients.");
        }
        return "redirect:/admin/produits";
    }
}
