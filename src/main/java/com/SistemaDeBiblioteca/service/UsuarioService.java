package com.sistemadebiblioteca.service;

import java.util.List;

import com.sistemadebiblioteca.dto.UsuarioRequestDTO;
import com.sistemadebiblioteca.dto.UsuarioResponseDTO;

public interface UsuarioService {
    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO);
    UsuarioResponseDTO obtenerUsuarioPorId(Long id);
    List<UsuarioResponseDTO> listarUsuariosActivos();
    void eliminarUsuario(Long id); // Baja lógica
}