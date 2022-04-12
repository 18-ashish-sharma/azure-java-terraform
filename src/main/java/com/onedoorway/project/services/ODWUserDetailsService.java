package com.onedoorway.project.services;

import com.onedoorway.project.model.ODWUserDetails;
import com.onedoorway.project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ODWUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public ODWUserDetailsService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.onedoorway.project.model.User user = userRepository.getByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user with the given email");
        }
        log.info("Loaded the user by username {}", username);
        return new ODWUserDetails(user);
    }
}
