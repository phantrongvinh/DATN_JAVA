package com.datn.project.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.datn.project.config.JwtFilter;
import com.datn.project.dto.LoginRequest;
import com.datn.project.dto.ProfileResponse;
import com.datn.project.dto.RegisterRequest;
import com.datn.project.entity.AuthProvider;
import com.datn.project.entity.Role;
import com.datn.project.entity.User;
import com.datn.project.entity.VerificationToken;
import com.datn.project.repository.IRoleRepository;
import com.datn.project.repository.IUserRepository;
import com.datn.project.repository.IVerificationTokenRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private MailService mailService;

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

    @Autowired
    private JwtBlackListService jwtBlackListService;

    @Autowired
    private IVerificationTokenRepository verificationTokenRepository;

    AuthService(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    @Transactional
    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Account already exists"));
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Password does not match"));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setBirthDay(request.getBirthDay());
        user.setActived(false);
        user.setAuthProvider(AuthProvider.LOCAL);

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        user.setRoles(roles);

        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        sendVerificationEmail(user, token);

        return ResponseEntity.ok("Register success, please check your email");
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<GrantedAuthority> roles = new ArrayList<>(userDetails.getAuthorities());

        String token = jwtService.generateToken(userDetails.getUsername(), roles);

        return ResponseEntity.ok(
                Map.of("token", token, "email", userDetails.getUsername(), "roles", roles));
    }

    @Override
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated"));
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            jwtBlackListService.blacklistToken(token);
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @Override
    public ResponseEntity<?> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmailWithRoles(email).orElseThrow(() -> new RuntimeException(
                "User not found"));
                        
        ProfileResponse response = new ProfileResponse();

        response.setEmail(email);
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setBirthDay(user.getBirthDay());

        return ResponseEntity.ok(response);
    }

    private void sendVerificationEmail(User user, String token) {
        String link = "http://localhost:8080/api/v1/auth/activate?token=" + token;

        mailService.sendVerificationEmail(user.getEmail(), "Activate Account", "Click here: " + link);

    }

    @Override
    public void activate(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException(
                        "Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Token verify expired");
        }

        User user = verificationToken.getUser();
        user.setActived(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

    }

    @Override
    @Transactional
    public void resendActivation(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "User not found"));

        if (user.isActived()) {

            throw new RuntimeException(
                    "Account already activated");
        }

        verificationTokenRepository.deleteAllByUser(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setToken(token);

        verificationToken.setUser(user);

        verificationToken.setExpiryDate(
                LocalDateTime.now().plusHours(24));

        verificationTokenRepository
                .save(verificationToken);

        sendVerificationEmail(user, token);
    }
}
