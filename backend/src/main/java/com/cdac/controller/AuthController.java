package com.cdac.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.cdac.dto.*;
import com.cdac.dto.*;
import com.cdac.entity.User;
import com.cdac.repository.UserRepository;
import com.cdac.security.JwtUtil;
import com.cdac.service.EmailService;
import com.cdac.temp.TempUser;

import jakarta.servlet.http.HttpSession;

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

        session.setAttribute("tempUser", tempUser);  // ✅ Store in session
        emailService.sendVerificationEmail(request.getEmail(), otp);



    
    // Verify: Validate OTP and Save User
    
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyRequest request, HttpSession session) {
        TempUser tempUser = (TempUser) session.getAttribute("tempUser");

        if (tempUser == null || !tempUser.getEmail().equals(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No registration found for this email.");
        }

	// @PostMapping("/verify")
	// public ResponseEntity<String> verify(@RequestBody VerifyRequest request) {
	// 	TempUser tempUser = tempUsers.get(request.getEmail());

    //     if (tempUser.getOtpExpiry().isBefore(LocalDateTime.now())) {
    //         session.removeAttribute("tempUser");
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP expired. Please register again.");
    //     }

    //     // ✅ Save to DB after successful verification
    //     User user = new User();
    //     user.setUsername(tempUser.getUsername());
    //     user.setEmail(tempUser.getEmail());
    //     user.setPassword(tempUser.getEncodedPassword());

    //     userRepo.save(user);
    //     session.removeAttribute("tempUser"); // ❌ Clear session after use

	// 	if (tempUser.getOtpExpiry().isBefore(LocalDateTime.now())) {
	// 		tempUsers.remove(request.getEmail());
	// 		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP expired. Please register again.");
	// 	}

    
    // Login
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepo.findByEmail(request.getEmail());

		userRepo.save(user);
		tempUsers.remove(request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

	    try {
	        // Try to authenticate user with email and password
	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
	    } catch (BadCredentialsException ex) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
	    }

	    // If authentication successful
	    String token = jwtUtil.generateToken(request.getEmail());
	    return ResponseEntity.ok(new JwtResponse(token, request.getEmail()));
	}
	
	
	
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPassword request, HttpSession session) {
		// Find user by email
		Optional<User> userOptional = userRepo.findByEmail(request.getEmail());

   
    // Generate 6-digit OTP
   
    private String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
