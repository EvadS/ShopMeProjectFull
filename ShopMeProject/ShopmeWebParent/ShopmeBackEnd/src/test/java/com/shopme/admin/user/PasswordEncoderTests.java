package com.shopme.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PasswordEncoderTests {

    @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "31323334";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.printf(encodedPassword);

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        assertThat(matches).isTrue();
    }
}
