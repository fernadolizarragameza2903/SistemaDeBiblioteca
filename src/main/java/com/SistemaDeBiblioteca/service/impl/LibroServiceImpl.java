package com.sistemadebiblioteca.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadebiblioteca.dto.LibroRequestDTO;
import com.sistemadebiblioteca.dto.LibroResponseDTO;
import com.sistemadebiblioteca.exception.CategoriaNoEncontradaException;
import com.sistemadebiblioteca.exception.IsbnDuplicadoException;
import com.sistemadebiblioteca.exception.LibroNoEncontradoException;
import com.sistemadebiblioteca.model.Categoria;
import com.sistemadebiblioteca.model.Libro;
import com.sistemadebiblioteca.repository.CategoriaRepository;
import com.sistemadebiblioteca.repository.LibroRepository;
import com.sistemadebiblioteca.service.LibroService;

@Service
@Transactional
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;

    public LibroServiceImpl(LibroRepository libroRepository, CategoriaRepository categoriaRepository) {
        this.libroRepository = libroRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public LibroResponseDTO crear(LibroRequestDTO request) {
        validarIsbnDisponible(request.getIsbn(), null);
        Categoria categoria = obtenerCategoriaSiCorresponde(request.getCategoriaId());

        Libro libro = new Libro();
        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setIsbn(request.getIsbn());
        libro.setCategoria(categoria);
        libro.setStock(request.getStock());
        libro.setActivo(true);

        return convertirAResponse(libroRepository.save(libro));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDTO> listarActivos() {
        return libroRepository.findByActivoTrue()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResponseDTO obtenerPorId(Long id) {
        return convertirAResponse(buscarLibroPorId(id));
    }

    @Override
    public LibroResponseDTO actualizar(Long id, LibroRequestDTO request) {
        Libro libro = buscarLibroPorId(id);
        validarIsbnDisponible(request.getIsbn(), id);
        Categoria categoria = obtenerCategoriaSiCorresponde(request.getCategoriaId());

        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setIsbn(request.getIsbn());
        libro.setCategoria(categoria);
        libro.setStock(request.getStock());

        return convertirAResponse(libroRepository.save(libro));
    }

    @Override
    public void darDeBaja(Long id) {
        Libro libro = buscarLibroPorId(id);
        libro.setActivo(false);
        libroRepository.save(libro);
    }

    private Libro buscarLibroPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new LibroNoEncontradoException("No se encontro el libro con id " + id));
    }

    private void validarIsbnDisponible(String isbn, Long idActual) {
        Optional<Libro> libroConMismoIsbn = libroRepository.findByIsbn(isbn);
        if (libroConMismoIsbn.isPresent() && !libroConMismoIsbn.get().getId().equals(idActual)) {
            throw new IsbnDuplicadoException("Ya existe un libro con el ISBN " + isbn);
        }
    }

    private Categoria obtenerCategoriaSiCorresponde(Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new CategoriaNoEncontradaException(
                        "No se encontro la categoria con id " + categoriaId));
    }

    private LibroResponseDTO convertirAResponse(Libro libro) {
        LibroResponseDTO response = new LibroResponseDTO();
        response.setId(libro.getId());
        response.setTitulo(libro.getTitulo());
        response.setAutor(libro.getAutor());
        response.setIsbn(libro.getIsbn());
        response.setNombreCategoria(libro.getCategoria() != null ? libro.getCategoria().getNombre() : null);
        response.setStock(libro.getStock());
        response.setActivo(libro.getActivo());
        response.setFechaCreacion(libro.getFechaCreacion());
        return response;
    }
}
