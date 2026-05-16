package fr.julien.charcuterieorders.controller.admin;

import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.repository.OrderItemRepository;
import fr.julien.charcuterieorders.service.ExportService;
import fr.julien.charcuterieorders.service.AdminOrderItemService;


import fr.julien.charcuterieorders.service.ProductService;
import fr.julien.charcuterieorders.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/admin/commandes")
@RequiredArgsConstructor
public class AdminOrderController {

    private final UserService userService;
    private final ProductService productService;
    private final AdminOrderItemService adminOrderItemService;
    private final ExportService exportService;

    @GetMapping
    public String index(Model model,
                        @RequestParam(defaultValue = "true") boolean seulementCommandes) {

        List<User> clients = new ArrayList<>(userService.getAllClients());

        clients.sort(
                Comparator
                        .comparing((User client) -> !"STOCK".equals(client.getInputMode()))
                        .thenComparing(User::getName)
        );
        List<OrderItem> items = adminOrderItemService.getAll();

        Map<Long, Map<Long, Integer>> quantities = new HashMap<>();
        for (OrderItem item : items) {
            quantities
                    .computeIfAbsent(item.getUser().getId(), k -> new HashMap<>())
                    .put(item.getProduct().getId(), item.getQuantity());
        }

        Comparator<Product> triProduits = Comparator
                .comparingInt((Product p) -> ProductService.CATEGORY_ORDER.indexOf(p.getCategory()))
                .thenComparing(Product::getName);

        List<Product> products;
        if (seulementCommandes) {
            products = items.stream()
                    .map(OrderItem::getProduct)
                    .distinct()
                    .sorted(triProduits)
                    .toList();
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("clients", clients);
        model.addAttribute("products", products);
        model.addAttribute("quantities", quantities);
        model.addAttribute("seulementCommandes", seulementCommandes);
        return "admin/commandes/index";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export() throws IOException {
        byte[] data = exportService.exportCommandes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=commandes.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @PostMapping
    public String store(@RequestParam Map<String, String> formData) {

        formData.forEach((key, value) -> {

            if (!key.startsWith("user_")) return;

            String[] parts = key.split("_");

            Long userId = Long.parseLong(parts[1]);
            Long productId = Long.parseLong(parts[3]);

            Integer quantity = value.isBlank() ? 0 : Integer.parseInt(value);

            System.out.println("SAVE OR UPDATE user=" + userId + " product=" + productId + " qty=" + quantity);
            adminOrderItemService.saveOrUpdate(userId, productId, quantity);
        });

        return "redirect:/admin/commandes";
    }



}