package fr.gaston.backend.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {


    //GET
    @GetMapping()
    public ResponseEntity<String> getUserInfo(Authentication authentication, @RequestParam String email) {
        // Récupérer l'utilisateur actuel basé sur l'authentification
        User currentUser = (User) authentication.getPrincipal();

        // Comparer le token avec le nom d'utilisateur
        if (email.equals(currentUser.getEmail())) {
            return ResponseEntity.ok("User info for: " + currentUser);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
    }
}
