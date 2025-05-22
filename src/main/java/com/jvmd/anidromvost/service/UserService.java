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
        try {
           return userRepo.getUserByName(username).toUserDetails();
        }catch (Exception e){
            log.error(e.getMessage());
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    public User findByUsername(String username) {
        return userRepo.getUserByName(username);
    }

    public User save(User user) {
        try {
            log.info("Saving user: {}", user);
            return userRepo.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    public boolean existsByUsername(String username) {
       try {
           log.info("Checking if user exists with username: {}", username);
           return userRepo.existsByName(username);
       }catch (Exception e){
           log.error(e.getMessage());
           throw new UsernameNotFoundException(e.getMessage());
       }
    }

    public boolean deleteByUsername(String username) {
         try {
             log.info("Delete user with username: {}", username);
             return userRepo.deleteByName(username);
         }catch (Exception e){
             log.error(e.getMessage());
             throw new UsernameNotFoundException(e.getMessage());
         }
    }
}
