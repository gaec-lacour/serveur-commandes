package fr.julien.charcuterieorders.controller.admin;

import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.service.OrderItemService;
import fr.julien.charcuterieorders.service.OrderService;
import fr.julien.charcuterieorders.service.ProductService;
import fr.julien.charcuterieorders.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/commandes/edit/")
@RequiredArgsConstructor

public class AdminOrderEditController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final OrderItemService orderItemService;


    @GetMapping("/{id}")
    public String index(@PathVariable Long id, Model model) {

        User user = userService.getById(id);

        // Produits accessibles groupés par catégorie
        Map<String, List<Product>> productsByCategory = user.getAccessibleProducts()
                .stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.groupingBy(Product::getCategory));
        // Quantités existantes indexées par productId pour affichage facile
        Map<Long, Integer> quantities = orderItemService.getByUser(user)
                .stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getId(),
                        OrderItem::getQuantity
                ));

        model.addAttribute("productsByCategory",
                productService.groupByCategory(user.getAccessibleProducts()));
        model.addAttribute("quantities", quantities);
        model.addAttribute("client", user);
        return "admin/commandes/edit";
    }

    @PostMapping("/{id}")
    public String store(@PathVariable Long id, @RequestParam Map<String, String> formData, RedirectAttributes redirectAttributes) {

        User user = userService.getById(id);

        List<OrderItem> items = orderItemService.getByUser(user);

        Map<Long, Integer> dbQuantities = items.stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getId(),
                        OrderItem::getQuantity
                ));
        for (Product p : user.getAccessibleProducts()) {
            dbQuantities.putIfAbsent(p.getId(), 0);
        }

        Map<Long, Integer> formQuantities = new HashMap<>();

        formData.forEach((key, value) -> {
            if (key.startsWith("product_")) {

                Long productId = Long.parseLong(key.replace("product_", ""));
                Integer quantity = value.isBlank() ? 0 : Integer.parseInt(value);

                formQuantities.put(productId, quantity);

            }
        });

        boolean changed = !formQuantities.equals(dbQuantities);

        if (!changed) {
            redirectAttributes.addFlashAttribute("error", "Aucune modification détectée sur la commande, enregistrement impossible");
            return "redirect:/admin/commandes";
        }

        formQuantities.forEach((productId, quantity) -> {

            Product product = user.getAccessibleProducts()
                    .stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElseThrow();

            orderItemService.saveOrUpdate(user, product, quantity);
        });

        orderService.createOrder(user);

        redirectAttributes.addFlashAttribute("success", "Commande enregistrée");

        return String.format("redirect:/admin/commandes/edit/%d", id);
    }
}
