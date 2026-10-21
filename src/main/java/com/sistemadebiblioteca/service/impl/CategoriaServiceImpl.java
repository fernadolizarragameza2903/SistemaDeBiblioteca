package com.sistemadebiblioteca.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadebiblioteca.dto.CategoriaRequestDTO;
import com.sistemadebiblioteca.dto.CategoriaResponseDTO;
import com.sistemadebiblioteca.dto.LibroResponseDTO;
import com.sistemadebiblioteca.exception.CategoriaNoEncontradaException;
import com.sistemadebiblioteca.exception.OperacionNoPermitidaException;
import com.sistemadebiblioteca.exception.RecursoDuplicadoException;
import com.sistemadebiblioteca.model.Categoria;
import com.sistemadebiblioteca.model.Libro;
import com.sistemadebiblioteca.repository.CategoriaRepository;
import com.sistemadebiblioteca.repository.LibroRepository;
import com.sistemadebiblioteca.service.CategoriaService;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final LibroRepository libroRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, LibroRepository libroRepository) {
        this.categoriaRepository = categoriaRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    public CategoriaResponseDTO crear(CategoriaRequestDTO request) {
        validarNombreDisponible(request.getNombre(), null);

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre().trim());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setActivo(true);

        return convertirAResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarActivas() {
        return categoriaRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(Long id) {
        return convertirAResponse(buscarCategoriaPorId(id));
    }

    @Override
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO request) {
        Categoria categoria = buscarCategoriaPorId(id);
        validarNombreDisponible(request.getNombre(), id);

        categoria.setNombre(request.getNombre().trim());
        categoria.setDescripcion(request.getDescripcion());

        return convertirAResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void darDeBaja(Long id) {
        Categoria categoria = buscarCategoriaPorId(id);
        boolean tieneLibrosActivos = libroRepository.findByCategoriaId(id)
                .stream()
                .anyMatch(Libro::getActivo);
        if (tieneLibrosActivos) {
            throw new OperacionNoPermitidaException(
                    "No se puede dar de baja la categoria porque tiene libros activos");
        }
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDTO> listarLibros(Long categoriaId) {
        Categoria categoria = buscarCategoriaPorId(categoriaId);
        return libroRepository.findByCategoriaId(categoriaId)
                .stream()
                .filter(Libro::getActivo)
                .map(libro -> {
                    LibroResponseDTO dto = new LibroResponseDTO();
                    dto.setId(libro.getId());
                    dto.setTitulo(libro.getTitulo());
                    dto.setAutor(libro.getAutor());
                    dto.setIsbn(libro.getIsbn());
                    dto.setNombreCategoria(categoria.getNombre());
                    dto.setStock(libro.getStock());
                    dto.setActivo(libro.getActivo());
                    dto.setFechaCreacion(libro.getFechaCreacion());
                    return dto;
                })
                .toList();
    }

    private Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("No se encontro la categoria con id " + id));
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        Optional<Categoria> existente = categoriaRepository.findByNombreIgnoreCase(nombre.trim());
        if (existente.isPresent() && !existente.get().getId().equals(idActual)) {
            throw new RecursoDuplicadoException("Ya existe una categoria con el nombre " + nombre);
        }
    }

    private CategoriaResponseDTO convertirAResponse(Categoria categoria) {
        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());
        response.setActivo(categoria.getActivo());
        return response;
    }
}
