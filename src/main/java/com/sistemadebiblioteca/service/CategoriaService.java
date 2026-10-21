package com.sistemadebiblioteca.service;

import java.util.List;

import com.sistemadebiblioteca.dto.CategoriaRequestDTO;
import com.sistemadebiblioteca.dto.CategoriaResponseDTO;
import com.sistemadebiblioteca.dto.LibroResponseDTO;

public interface CategoriaService {

    CategoriaResponseDTO crear(CategoriaRequestDTO request);

    List<CategoriaResponseDTO> listarActivas();

    CategoriaResponseDTO obtenerPorId(Long id);

    CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO request);

    void darDeBaja(Long id);

    List<LibroResponseDTO> listarLibros(Long categoriaId);
}
