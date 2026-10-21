package com.sistemadebiblioteca.service;

import java.util.List;

import com.sistemadebiblioteca.dto.PrestamoRequestDTO;
import com.sistemadebiblioteca.dto.PrestamoResponseDTO;

public interface PrestamoService {

    PrestamoResponseDTO registrar(PrestamoRequestDTO request);

    PrestamoResponseDTO devolver(Long id);

    PrestamoResponseDTO obtenerPorId(Long id);

    List<PrestamoResponseDTO> listarActivos();

    List<PrestamoResponseDTO> listarVencidos();

    List<PrestamoResponseDTO> listarPorUsuario(Long usuarioId);
}
