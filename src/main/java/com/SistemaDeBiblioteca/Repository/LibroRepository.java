package com.sistemadebiblioteca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemadebiblioteca.model.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByActivoTrue();

    Optional<Libro> findByIsbn(String isbn);

    List<Libro> findByCategoriaId(Long categoriaId);
}

