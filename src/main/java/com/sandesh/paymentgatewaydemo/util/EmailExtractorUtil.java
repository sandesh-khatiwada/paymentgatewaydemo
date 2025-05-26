package com.sandesh.paymentgatewaydemo.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class EmailExtractorUtil {

    public static String getEmailFromJwt() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Email is in 'sub'
        }
        throw new IllegalStateException("User not authenticated or invalid principal");
    }
}
