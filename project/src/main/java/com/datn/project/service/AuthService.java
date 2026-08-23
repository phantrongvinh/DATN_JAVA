package com.datn.project.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
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
import com.datn.project.dto.auth.LoginRequest;
import com.datn.project.dto.auth.ProfileResponse;
import com.datn.project.dto.auth.RegisterRequest;
import com.datn.project.dto.auth.UpdateProfileRequest;
import com.datn.project.entity.AuthProvider;
import com.datn.project.entity.ForgotPasswordToken;
import com.datn.project.entity.Role;
import com.datn.project.entity.User;
import com.datn.project.entity.VerificationToken;
import com.datn.project.repository.IForgotPasswordTokenRepository;
import com.datn.project.repository.IOrderRepository;
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

    @Autowired
    private IForgotPasswordTokenRepository forgotPasswordToken;

    @Autowired
    private IOrderRepository orderRepository;

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    // Hàm generate token
    private String generateShortToken(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    // hàm và luồng xử lý tạo tài khoản -> gửi mail kích hoạt khi đăng ký thành công
    @Override
    @Transactional
    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Tài khoản có email này đã tồn tại"));
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Không đúng mật khẩu"));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setActived(false);
        user.setAuthProvider(AuthProvider.LOCAL);

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        user.setRoles(roles);

        userRepository.save(user);

        String token = generateShortToken(8);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        sendVerificationEmail(user, token);

        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công, kiểm tra email để kích hoạt tài khoản"));
    }

    // hàm xử lý và validate đăng nhập, kiểm tra tồn tại email và đúng password
    @Override
    public ResponseEntity<?> login(LoginRequest request) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<GrantedAuthority> roles = new ArrayList<>(userDetails.getAuthorities());

        String token = jwtService.generateToken(userDetails.getUsername(), roles);

        return ResponseEntity.ok(
                Map.of("token", token));
    }

    // hàm xử lý đăng xuất, ghi token phiên đăng nhập vào blacklist tránh bị rò rỉ
    // đăng nhập bằng token, và xóa token khỏi author
    @Override
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Chưa đăng nhập"));
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            jwtBlackListService.blacklistToken(token);
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }

    // hàm xử lý nội bộ thông tin bậc khách hàng
    private static final long TIER_SILVER = 5_000_000L;
    private static final long TIER_GOLD = 20_000_000L;
    private static final long TIER_DIAMOND = 50_000_000L;

    private String resolveMemberTier(long spending) {
        if (spending >= TIER_DIAMOND)
            return "Kim Cương";
        if (spending >= TIER_GOLD)
            return "Vàng";
        if (spending >= TIER_SILVER)
            return "Bạc";
        return "Đồng";
    }

    private String resolveNextTierName(long spending) {
        if (spending < TIER_SILVER)
            return "Bạc";
        if (spending < TIER_GOLD)
            return "Vàng";
        if (spending < TIER_DIAMOND)
            return "Kim Cương";
        return null; // đã ở hạng cao nhất
    }

    private BigDecimal resolveAmountToNextTier(long spending) {
        long threshold;
        if (spending < TIER_SILVER)
            threshold = TIER_SILVER;
        else if (spending < TIER_GOLD)
            threshold = TIER_GOLD;
        else if (spending < TIER_DIAMOND)
            threshold = TIER_DIAMOND;
        else
            return null; // đã ở hạng cao nhất, không còn "hạng tiếp theo"

        return BigDecimal.valueOf(threshold - spending);
    }

    // hàm lấy thông tin người dùng khi đã đăng nhập thành công
    @Override
    public ResponseEntity<?> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        ProfileResponse response = new ProfileResponse();
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setBirthDay(user.getBirthDay() != null ? user.getBirthDay(): null);
        response.setBirthDayEditable(user.getBirthDay() == null);

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        response.setRoles(roles);

        Integer totalOrders = orderRepository.countByUserIdExcludingCancelled(user.getId());
        response.setTotalOrders(totalOrders);
        response.setLoyaltyPoints(user.getLoyaltyPoints());

        long spending = (long) user.getLoyaltyPoints() * 10_000; 
        response.setMemberTier(resolveMemberTier(spending));
        response.setNextTierName(resolveNextTierName(spending));
        response.setAmountToNextTier(resolveAmountToNextTier(spending));

        return ResponseEntity.ok(response);
    }

    // hàm cập nhật profile user, xử lý chỉ được cập nhật ngày sinh 1 lần
    @Override
    @Transactional
    public ResponseEntity<?> updateProfile(UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }

        if (request.getBirthDay() != null) {
            if (user.getBirthDay() != null) {
                throw new RuntimeException("Ngày sinh chỉ được cập nhật một lần và đã được thiết lập trước đó");
            }
            user.setBirthDay(request.getBirthDay());
        }

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Cập nhật hồ sơ thành công"));
    }

    // hàm gửi mail kích hoạt tài khoản
    private void sendVerificationEmail(User user, String token) {
        String link = "https://sports-ecommerce-production.up.railway.app/api/v1/auth/activate?token=" + token;

        mailService.sendActivationEmail(user, link);
    }

    // hàm kích hoạt tài khoản bằng cách gửi token vào mail và người dùng xác nhận
    // dường dẫn, kiểm tra param token có trong db verifitoken
    @Override
    public void activate(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException(
                        "Mã kích hoạt không hợp lệ"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Mã kích hoạt hết hạn");
        }

        User user = verificationToken.getUser();
        user.setActived(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

    }

    // hàm và luồng xử lý gửi lại mã kích hoạt nếu đã time out mã kích hoạt cũ
    @Override
    @Transactional
    public void resendActivation(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Người dùng không tìm thấy"));

        if (user.isActived()) {

            throw new RuntimeException(
                    "Tài khoản đã được kích hoạt");
        }

        verificationTokenRepository.deleteAllByUser(user);

        String token = generateShortToken(8);

        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setToken(token);

        verificationToken.setUser(user);

        verificationToken.setExpiryDate(
                LocalDateTime.now().plusHours(24));

        verificationTokenRepository
                .save(verificationToken);

        sendVerificationEmail(user, token);
    }

    // hàm xử lý quên mật khẩu, gửi mail có param token, có time out token, có xóa
    // token cũ nếu bấm gửi lại
    @Override
    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Tài khoản có email này không tồn tại"));

        ForgotPasswordToken oldResetToken = forgotPasswordToken.findByEmail(user.getEmail());

        if (oldResetToken != null) {
            forgotPasswordToken.delete(oldResetToken);
        }

        String token = generateShortToken(8);

        ForgotPasswordToken resetToken = new ForgotPasswordToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        forgotPasswordToken.save(resetToken);

        // Gửi mail
        sendResetPassword(user, token);
        return ResponseEntity.ok("Email đã được gửi");
    }

    // hàm gửi mail token param reset password
    private void sendResetPassword(User user, String token) {
        String link = "https://sports-ecommerce-nine.vercel.app/reset-password?token=" + token;
        mailService.sendResetPasswordEmail(user, link);
    }

    // hàm xử lý cập nhật mật khẩu mới, kiểm tra param hợp lệ với token trong db sẽ
    // được cập nhật mật khẩu mới
    @Override
    public ResponseEntity<?> resetPassword(String token, String newPassword) {
        ForgotPasswordToken resetToken = forgotPasswordToken.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Mã khôi phục hết hạng, vui lòng gửi lại yêu cầu"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Hết thời gian khôi phục, vui lòng gửi lại yêu cầu");
        }

        User user = userRepository.findByEmail(resetToken.getEmail()).get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        forgotPasswordToken.delete(resetToken);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
}
