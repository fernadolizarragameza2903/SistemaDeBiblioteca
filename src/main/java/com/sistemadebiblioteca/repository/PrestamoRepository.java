package com.sistemadebiblioteca.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemadebiblioteca.model.EstadoPrestamo;
import com.sistemadebiblioteca.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByEstadoOrderByFechaPrestamoDesc(EstadoPrestamo estado);

    List<Prestamo> findByUsuarioIdOrderByFechaPrestamoDesc(Long usuarioId);

    long countByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    boolean existsByUsuarioIdAndLibroIdAndEstado(Long usuarioId, Long libroId, EstadoPrestamo estado);

    List<Prestamo> findByEstadoAndFechaDevolucionEsperadaBefore(EstadoPrestamo estado, LocalDate fecha);
}
