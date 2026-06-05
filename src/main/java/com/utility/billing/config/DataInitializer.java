package com.utility.billing.config;

import com.utility.billing.entity.AppUser;
import com.utility.billing.enums.Role;
import com.utility.billing.enums.Status;
import com.utility.billing.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        upsertUser("Jolie Princesse Ishimwe", "jolieprincesseishimwe@gmail.com", "+250785060644", "Jolie@123", Role.ROLE_ADMIN);
    }

    private void upsertUser(String name, String email, String phone, String rawPassword, Role role) {
        AppUser user = userRepository.findByEmail(email).orElseGet(AppUser::new);
        user.setFullName(name);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setStatus(Status.ACTIVE);
        user.setRole(role);
        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
