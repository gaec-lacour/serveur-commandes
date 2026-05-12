package fr.julien.charcuterieorders.service;

import fr.julien.charcuterieorders.model.User;
import fr.julien.charcuterieorders.repository.OrderItemRepository;
import fr.julien.charcuterieorders.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public List<User> getAllClients() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().equals("CLIENT"))
                .toList();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(User existing, User form) {
        existing.setName(form.getName());
        existing.setEmail(form.getEmail());

        // On ne rehashe que si un nouveau mot de passe est saisi
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        existing.setAccessibleProducts(form.getAccessibleProducts());
        return userRepository.save(existing);
    }
    @Transactional
    public void delete(Long id) {
        User user = getById(id);
        orderItemRepository.deleteByUser(user);  // supprime les commandes d'abord
        userRepository.deleteById(id);           // puis le client
    }
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
