package fr.julien.charcuterieorders.controller.admin;

import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/produits")
@RequiredArgsConstructor

public class AdminProductController {
    private final ProductService productService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", productService.getAllProducts());
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
    public String update(@PathVariable Long id, @ModelAttribute Product product) {
        product.setId(id);
        productService.save(product);
        return "redirect:/admin/produits";
    }

    @PostMapping("/{id}/supprimer")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/produits";
    }
}
