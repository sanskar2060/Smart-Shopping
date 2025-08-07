package com.cdac.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.cdac.dto.*;
import com.cdac.entity.User;
import com.cdac.repository.UserRepository;
import com.cdac.security.JwtUtil;
import com.cdac.service.EmailService;
import com.cdac.temp.TempUser;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @GetMapping("/test")
    public String testing() {
        return "Hello Chirag";
    }

    
    // Register: Store in session + send OTP
   
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request, HttpSession session) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already registered.");
        }


        TempUser sessionTempUser = (TempUser) session.getAttribute("tempUser");
        if (sessionTempUser != null && sessionTempUser.getEmail().equals(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already pending verification.");
        }


        String otp = generateOTP();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        TempUser tempUser = new TempUser(
            request.getUsername(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            otp,
            expiry
        );

        session.setAttribute("tempUser", tempUser);  // ✅ Store in session
        emailService.sendVerificationEmail(request.getEmail(), otp);

        return ResponseEntity.ok("OTP sent to your email. Please verify to complete registration.");
    }

    
    // Verify: Validate OTP and Save User
    
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyRequest request, HttpSession session) {
        TempUser tempUser = (TempUser) session.getAttribute("tempUser");

        if (tempUser == null || !tempUser.getEmail().equals(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No registration found for this email.");
        }

        if (!tempUser.getOtp().equals(request.getOtp_code())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }

        if (tempUser.getOtpExpiry().isBefore(LocalDateTime.now())) {
            session.removeAttribute("tempUser");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP expired. Please register again.");
        }

        // ✅ Save to DB after successful verification
        User user = new User();
        user.setUsername(tempUser.getUsername());
        user.setEmail(tempUser.getEmail());
        user.setPassword(tempUser.getEncodedPassword());

        userRepo.save(user);
        session.removeAttribute("tempUser"); // ❌ Clear session after use

        return ResponseEntity.ok("Email verified and user registered successfully.");
    }

    
    // Login
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepo.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtUtil.generateToken(request.getEmail());

        return ResponseEntity.ok(new JwtResponse(token, request.getEmail()));
    }

   
    // Generate 6-digit OTP
   
    private String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
