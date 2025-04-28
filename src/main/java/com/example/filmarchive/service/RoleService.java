package com.example.filmarchive.service;

import com.example.filmarchive.entity.Role;
import com.example.filmarchive.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(String name) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        return roleOpt.orElse(null);
    }
}
