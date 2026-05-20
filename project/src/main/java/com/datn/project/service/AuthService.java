package com.datn.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.datn.project.config.JwtFilter;
import com.datn.project.dto.LoginRequest;
import com.datn.project.dto.RegisterRequest;
import com.datn.project.entity.Role;
import com.datn.project.entity.User;
import com.datn.project.repository.IRoleRepository;
import com.datn.project.repository.IUserRepository;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Account already exists with email: " + request.getEmail());
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password does not match");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        user.setRoles(roles);;

        userRepository.save(user);

        return ResponseEntity.ok("Register success!!!");
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<GrantedAuthority> roles = new ArrayList<>(userDetails.getAuthorities());

        String token = jwtService.generateToken(userDetails.getUsername(), roles);

        return ResponseEntity.ok(
                Map.of("token", token, "email", userDetails.getUsername(), "roles", authentication.getAuthorities()));
    }
}
