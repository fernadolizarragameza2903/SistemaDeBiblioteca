package com.sistemadebiblioteca.service;

import com.SistemaDeBiblioteca.dto.UsuarioRequestDTO;
import com.SistemaDeBiblioteca.dto.UsuarioResponseDTO;
import java.util.List;

public interface UsuarioService {
    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO);
    UsuarioResponseDTO obtenerUsuarioPorId(Long id);
    List<UsuarioResponseDTO> listarUsuariosActivos();
    void eliminarUsuario(Long id); // Baja lógica
}