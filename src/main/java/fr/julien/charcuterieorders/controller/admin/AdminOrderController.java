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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/admin/commandes")
@RequiredArgsConstructor
@SessionAttributes("draftQuantities")

public class AdminOrderController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderItemService orderItemService;
    private final ExportService exportService;

    @ModelAttribute("draftDirty")
    public Boolean draftDirty() {
        return false;
    }

    @ModelAttribute("draftQuantities")
    public Map<Long, Map<Long, Integer>> initDraft() {
        return new HashMap<>();
    }

    @GetMapping
    public String index(Model model,
                        @RequestParam(defaultValue = "true") boolean seulementCommandes,
                        @ModelAttribute("draftQuantities") Map<Long, Map<Long, Integer>> draft) {

        List<User> clients = new ArrayList<>(userService.getAllClients());

        clients.sort(
                Comparator
                        .comparing((User client) -> !"STOCK".equals(client.getInputMode()))
                        .thenComparing(User::getName)
        );

        List<OrderItem> items = orderItemService.getAll();

        // INITIALISATION UNIQUEMENT SI VIDE
        if (draft.isEmpty()) {
            for (OrderItem item : items) {
                draft
                        .computeIfAbsent(item.getUser().getId(), k -> new HashMap<>())
                        .put(item.getProduct().getId(), item.getQuantity());
            }
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
        model.addAttribute("draftQuantities", draft);
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

    @PostMapping("/decrement")
    public String decrement(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @ModelAttribute("draftQuantities") Map<Long, Map<Long, Integer>> draft,
            @RequestParam(defaultValue = "true") boolean seulementCommandes
    ) {

        draft.computeIfAbsent(userId, k -> new HashMap<>())
                .computeIfPresent(productId, (k, v) -> Math.max(v - 1, 0));

        return "redirect:/admin/commandes?seulementCommandes=" + seulementCommandes;
    }
}