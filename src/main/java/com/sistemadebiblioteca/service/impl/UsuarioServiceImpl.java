package com.sistemadebiblioteca.service.impl;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadebiblioteca.dto.UsuarioRequestDTO;
import com.sistemadebiblioteca.dto.UsuarioResponseDTO;
import com.sistemadebiblioteca.exception.RecursoDuplicadoException;
import com.sistemadebiblioteca.exception.RecursoNoEncontradoException;
import com.sistemadebiblioteca.model.Rol;
import com.sistemadebiblioteca.model.Usuario;
import com.sistemadebiblioteca.repository.RolRepository;
import com.sistemadebiblioteca.repository.UsuarioRepository;
import com.sistemadebiblioteca.service.UsuarioService;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO) {
        if (usuarioRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new RecursoDuplicadoException("El email " + requestDTO.getEmail() + " ya esta registrado");
        }

        Rol rol = rolRepository.findById(requestDTO.getRolId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontro el rol con id " + requestDTO.getRolId()));

        Usuario usuario = new Usuario();
        usuario.setNombre(requestDTO.getNombre());
        usuario.setApellido(requestDTO.getApellido());
        usuario.setEmail(requestDTO.getEmail());
        // Encriptar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        usuario.setRol(rol);
        usuario.setActivo(true);

        return mapearAResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {
        return mapearAResponse(buscarUsuarioPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el usuario con id " + id));
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
