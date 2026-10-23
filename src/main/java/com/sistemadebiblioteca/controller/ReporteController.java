package com.sistemadebiblioteca.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemadebiblioteca.model.EstadoPrestamo;
import com.sistemadebiblioteca.repository.CategoriaRepository;
import com.sistemadebiblioteca.repository.LibroRepository;
import com.sistemadebiblioteca.repository.PrestamoRepository;
import com.sistemadebiblioteca.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoRepository prestamoRepository;

    public ReporteController(LibroRepository libroRepository,
                             CategoriaRepository categoriaRepository,
                             UsuarioRepository usuarioRepository,
                             PrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Integer>> resumen() {
        Map<String, Integer> resumen = new LinkedHashMap<>();
        resumen.put("librosActivos", libroRepository.findByActivoTrue().size());
        resumen.put("categoriasActivas", categoriaRepository.findByActivoTrueOrderByNombreAsc().size());
        resumen.put("usuariosActivos", usuarioRepository.findByActivoTrue().size());
        resumen.put("prestamosActivos",
                prestamoRepository.findByEstadoOrderByFechaPrestamoDesc(EstadoPrestamo.ACTIVO).size());
        resumen.put("prestamosVencidos",
                prestamoRepository.findByEstadoAndFechaDevolucionEsperadaBefore(
                        EstadoPrestamo.ACTIVO, LocalDate.now()).size());
        return ResponseEntity.ok(resumen);
    }
}
