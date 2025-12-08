package org.example.orderservice.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.example.orderservice.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@Data
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    Role role;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(()-> "ROLE_" + role.toString());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
