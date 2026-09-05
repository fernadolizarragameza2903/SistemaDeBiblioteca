package com.sistemadebiblioteca.service;

import java.util.List;

import com.sistemadebiblioteca.dto.LibroRequestDTO;
import com.sistemadebiblioteca.dto.LibroResponseDTO;

public interface LibroService {

    LibroResponseDTO crear(LibroRequestDTO request);

    List<LibroResponseDTO> listarActivos();

    LibroResponseDTO obtenerPorId(Long id);

    LibroResponseDTO actualizar(Long id, LibroRequestDTO request);

    void darDeBaja(Long id);
}
