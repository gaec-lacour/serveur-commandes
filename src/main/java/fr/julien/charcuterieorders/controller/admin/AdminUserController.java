package fr.julien.charcuterieorders.controller.admin;

import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.service.ProductService;
import fr.julien.charcuterieorders.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/clients")
@RequiredArgsConstructor

public class AdminUserController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("clients", userService.getAllClients());
        return "admin/clients/index";
    }
    @GetMapping("/nouveau")
    public String create(Model model) {
        model.addAttribute("client", new User());
        model.addAttribute("allProducts", productService.getActiveProducts());
        return "admin/clients/form";
    }

    @PostMapping
    public String store(@ModelAttribute User client,
                        @RequestParam(required = false) List<Long> productIds) {
        attachProducts(client, productIds);
        client.setRole("CLIENT");
        userService.save(client);
        return "redirect:/admin/clients";
    }

    @GetMapping("/{id}/modifier")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("client", userService.getById(id));
        model.addAttribute("allProducts", productService.getActiveProducts());
        return "admin/clients/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute User form,
                         @RequestParam(required = false) List<Long> productIds) {
        User existing = userService.getById(id);
        attachProducts(form, productIds);
        userService.update(existing, form);
        return "redirect:/admin/clients";
    }

    @PostMapping("/{id}/supprimer")
    public String delete(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin/clients";
    }

    private void attachProducts(User client, List<Long> productIds) {
        if (productIds != null) {
            List<Product> products = productIds.stream()
                    .map(productService::getById)
                    .collect(Collectors.toCollection(ArrayList::new));  // ← liste mutable
            client.setAccessibleProducts(products);
        } else {
            client.setAccessibleProducts(new ArrayList<>());
        }
    }
}
