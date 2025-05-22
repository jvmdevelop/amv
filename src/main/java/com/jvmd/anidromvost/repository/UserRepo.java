package com.jvmd.anidromvost.repository;

import com.jvmd.anidromvost.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User getUserByName(String name);

    boolean existsByName(String name);

    boolean deleteByName(String name);
}
