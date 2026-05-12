package fr.julien.charcuterieorders.controller;

import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.service.OrderItemService;
import fr.julien.charcuterieorders.service.ProductService;
import fr.julien.charcuterieorders.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/commandes")
@RequiredArgsConstructor

public class OrderController {

    private final UserService userService;
    private final OrderItemService orderItemService;
    private final ProductService productService;

    @GetMapping
    public String index(Model model,
                        @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getByEmail(userDetails.getUsername());

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
                productService.groupByCategory(user.getAccessibleProducts()));model.addAttribute("quantities", quantities);
        return "commandes/index";
    }

    @PostMapping
    public String store(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam Map<String, String> formData) {

        User user = userService.getByEmail(userDetails.getUsername());

        // Le formulaire envoie product_1=3, product_2=0, etc.
        formData.forEach((key, value) -> {
            if (key.startsWith("product_")) {
                Long productId = Long.parseLong(key.replace("product_", ""));
                Integer quantity = value.isBlank() ? 0 : Integer.parseInt(value);

                Product product = user.getAccessibleProducts()
                        .stream()
                        .filter(p -> p.getId().equals(productId))
                        .findFirst()
                        .orElseThrow();

                orderItemService.saveOrUpdate(user, product, quantity);
            }
        });

        return "redirect:/commandes";
    }
}
