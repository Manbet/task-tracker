package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
