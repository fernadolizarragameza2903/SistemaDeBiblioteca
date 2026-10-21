package com.sistemadebiblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemadebiblioteca.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
