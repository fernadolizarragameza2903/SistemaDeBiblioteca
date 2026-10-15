package com.sistemadebiblioteca.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistemadebiblioteca.dto.UsuarioRequestDTO;
import com.sistemadebiblioteca.dto.UsuarioResponseDTO;
import com.sistemadebiblioteca.model.Rol;
import com.sistemadebiblioteca.model.Usuario;
import com.sistemadebiblioteca.Repository.RolRepository;
import com.sistemadebiblioteca.Repository.UsuarioRepository;
import com.sistemadebiblioteca.service.UsuarioService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository; // Asume que este repositorio existe

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO) {
        if (usuarioRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        Rol rol = rolRepository.findById(requestDTO.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(requestDTO.getNombre());
        usuario.setApellido(requestDTO.getApellido());
        usuario.setEmail(requestDTO.getEmail());
        // Encriptar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        usuario.setRol(rol);
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);
        return mapearAResponse(guardado);
    }

    @Override
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapearAResponse(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuariosActivos() {
        return usuarioRepository.findAll().stream()
                .filter(Usuario::getActivo)
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDTO mapearAResponse(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setNombreRol(usuario.getRol().getNombre());
        dto.setActivo(usuario.getActivo());
        return dto;
    }
}