package com.cdac.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cdac.dto.RegisterRequest;
import com.cdac.dto.VerifyRequest;
import com.cdac.entity.User;
import com.cdac.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public void registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
      //  user.setVerified(false);

        String otp = generateOTP();
        user.setVerificationCode(otp);
        user.setCodeExpiryTime(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), otp);
    }

    public void verifyEmail(VerifyRequest request) {
        System.out.println("Verifying email: " + request.getEmail());
        System.out.println("Provided code: " + request.getOtp_code());

        User user = userRepository.findByEmailAndVerificationCode(
                request.getEmail().trim(), request.getOtp_code().trim())
            .orElseThrow(() -> new RuntimeException("Invalid code or email"));

        System.out.println("User found: " + user.getEmail() + ", Code in DB: " + user.getVerificationCode());

        if (user.getCodeExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

       // user.setVerified(true);
        user.setVerificationCode(null);
        user.setCodeExpiryTime(null);
        userRepository.save(user);
    }


    private String generateOTP() {
        return String.valueOf((int)(Math.random() * 900000) + 100000); 
    }

//    public boolean isUserVerified(String email) {
//        return userRepository.findByEmail(email)
//            .map(User::isVerified)
//            .orElse(false);
//    }
}
