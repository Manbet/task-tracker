package org.example.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class User implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
//    private final UserRole role;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> authorities = role.getPermissions().stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.name()))
//                .collect(Collectors.toList());
//
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
//
//        return authorities;
//    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
