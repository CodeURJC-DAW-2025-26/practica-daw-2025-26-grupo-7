package com.fuegolento.backend.security;

import java.util.ArrayList;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fuegolento.backend.model.User;
import com.fuegolento.backend.repository.UserRepository;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    public RepositoryUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getEncodedPassword(), authorities);
    } 
    
}
