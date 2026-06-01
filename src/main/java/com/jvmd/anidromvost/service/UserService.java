package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.User;
import com.jvmd.anidromvost.repository.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user.toUserDetails();
    }

    public User findByUsername(String username) {
        return userRepo.getUserByName(username);
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepo.existsByName(username);
    }

    public boolean deleteByUsername(String username) {
        return userRepo.deleteByName(username);
    }
}
