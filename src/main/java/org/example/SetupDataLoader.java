package org.example;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.entities.PrivilegeEntity;
import org.example.entities.RoleEntity;
import org.example.entities.UserEntity;
import org.example.repositories.PrivilegeRepository;
import org.example.repositories.RoleRepository;
import org.example.repositories.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    boolean alreadySetup = false;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
        if (alreadySetup)
            return;
        PrivilegeEntity readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        PrivilegeEntity writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<PrivilegeEntity> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Collections.singletonList(readPrivilege));

        RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN");
        UserEntity user = new UserEntity();
        user.setName("Test");
        user.setSurname("Test");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEmail("test@test.com");
        user.setRoles(Collections.singletonList(adminRole));
        user.setActive(true);
        userRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    PrivilegeEntity createPrivilegeIfNotFound(String name) {
        PrivilegeEntity privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new PrivilegeEntity(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    void createRoleIfNotFound(String name, List<PrivilegeEntity> privileges) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
    }
}
