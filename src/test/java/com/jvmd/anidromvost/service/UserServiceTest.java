package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.ERole;
import com.jvmd.anidromvost.model.User;
import com.jvmd.anidromvost.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("testuser")
                .email("test@test.com")
                .password("hashedpassword")
                .role(ERole.USER)
                .build();
    }

    @Test
    void loadUserByUsername_found() {
        when(userRepo.getUserByName("testuser")).thenReturn(user);

        UserDetails details = userService.loadUserByUsername("testuser");

        assertEquals("testuser", details.getUsername());
        assertEquals("hashedpassword", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_notFound_throws() {
        when(userRepo.getUserByName("nobody")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nobody"));
    }

    @Test
    void findByUsername_returnsUser() {
        when(userRepo.getUserByName("testuser")).thenReturn(user);

        User result = userService.findByUsername("testuser");

        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getName());
    }

    @Test
    void existsByUsername_true() {
        when(userRepo.existsByName("testuser")).thenReturn(true);

        assertTrue(userService.existsByUsername("testuser"));
    }

    @Test
    void existsByUsername_false() {
        when(userRepo.existsByName("nobody")).thenReturn(false);

        assertFalse(userService.existsByUsername("nobody"));
    }
}
