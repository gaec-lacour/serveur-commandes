package fr.julien.charcuterieorders.controller.admin;

import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.service.ExportService;
import fr.julien.charcuterieorders.service.OrderItemService;
import fr.julien.charcuterieorders.service.ProductService;
import fr.julien.charcuterieorders.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/commandes")
@RequiredArgsConstructor
public class AdminOrderController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderItemService orderItemService;
    private final ExportService exportService;

    @GetMapping
    public String index(Model model,
                        @RequestParam(defaultValue = "true") boolean seulementCommandes) {

        List<User> clients = userService.getAllClients();
        List<OrderItem> items = orderItemService.getAll();

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
}