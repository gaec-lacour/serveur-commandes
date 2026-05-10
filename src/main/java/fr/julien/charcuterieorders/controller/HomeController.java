package fr.julien.charcuterieorders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import fr.julien.charcuterieorders.service.ProductService;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final ProductService productService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();  // l'email connecté
        model.addAttribute("products", productService.getAllProducts());
        return "home";
    }
}
