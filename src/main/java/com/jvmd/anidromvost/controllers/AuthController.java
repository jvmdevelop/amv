package com.jvmd.anidromvost.controllers;


import com.jvmd.anidromvost.model.ERole;
import com.jvmd.anidromvost.model.PUserDetails;
import com.jvmd.anidromvost.model.User;
import com.jvmd.anidromvost.service.UserService;
import com.jvmd.anidromvost.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/public/auth")
@AllArgsConstructor
public class AuthController {
    private  UserService userService;
    private  JwtUtil jwtUtil;


    @GetMapping("/me")
    public ResponseEntity<User> me(@AuthenticationPrincipal Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, String>> login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap<String, Object> result = new HashMap<>();

        String username = request.getParameter("username");
        String password = request.getParameter("password");


        String token;
        try {
            User user = userService.findByUsername(username);
            Authentication auth = new UsernamePasswordAuthenticationToken(user, password);
            token = jwtUtil.generateJwtToken(auth);

            response.addHeader("Authorization", "Bearer " + token);
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("status", HttpStatus.UNAUTHORIZED.value());
            return new ResponseEntity(result, HttpStatus.UNAUTHORIZED);
        }

        result.put("auth", true);
        result.put("token", token);

        return new ResponseEntity(result, HttpStatus.OK);

    }

    @PostMapping("/auth")
    public ResponseEntity<HashMap<String, Object>> auth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap<String, Object> result = new HashMap<>();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        ERole role = ERole.USER;
        String token;
        try {
            User user = userService.save(User.builder().name(username).password(password).role(role).email(email).build());

            UserDetails userDetails = user.toUserDetails();

            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            token = jwtUtil.generateJwtToken(auth);

            response.addHeader("Authorization", "Bearer " + token);
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("status", HttpStatus.UNAUTHORIZED.value());
            return new ResponseEntity(result, HttpStatus.UNAUTHORIZED);
        }
        result.put("auth", true);
        result.put("token", token);

        return new ResponseEntity(result, HttpStatus.OK);


    }


}
