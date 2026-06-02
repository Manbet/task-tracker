package org.example;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class SecurityContextUtil {
    public Optional<UserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ofNullable(authentication)
                .filter(a -> a.isAuthenticated() && !(a instanceof AnonymousAuthenticationToken))
                .map(a -> a.getPrincipal() instanceof UserDetails ud ? ud : null);
    }

    public boolean hasAuthority(String authority) {
        return getCurrentUser()
                .map(UserDetails::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null)
                ? authentication.getName()
                : null;
    }
}
