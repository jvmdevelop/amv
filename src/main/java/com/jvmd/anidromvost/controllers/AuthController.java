package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.ERole;
import com.jvmd.anidromvost.model.User;
import com.jvmd.anidromvost.service.UserService;
import com.jvmd.anidromvost.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/auth")
@AllArgsConstructor
public class AuthController {
    private UserService userService;
    private JwtUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<User> me(@AuthenticationPrincipal Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            String token = jwtUtil.generateJwtToken(auth);

            response.addHeader("Authorization", "Bearer " + token);
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            result.put("auth", true);
            result.put("token", token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("error", "Invalid username or password");
            result.put("status", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (userService.existsByUsername(username)) {
                result.put("error", "Username already exists");
                result.put("status", HttpStatus.CONFLICT.value());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
            }

            User user = userService.save(User.builder()
                    .name(username)
                    .password(passwordEncoder.encode(password))
                    .role(ERole.USER)
                    .email(email)
                    .build());

            UserDetails userDetails = user.toUserDetails();
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String token = jwtUtil.generateJwtToken(auth);

            response.addHeader("Authorization", "Bearer " + token);
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            result.put("auth", true);
            result.put("token", token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
