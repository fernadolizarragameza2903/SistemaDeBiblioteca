package com.sistemadebiblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadebiblioteca.dto.CategoriaRequestDTO;
import com.sistemadebiblioteca.dto.LibroRequestDTO;
import com.sistemadebiblioteca.dto.LibroResponseDTO;
import com.sistemadebiblioteca.exception.CategoriaNoEncontradaException;
import com.sistemadebiblioteca.exception.IsbnDuplicadoException;
import com.sistemadebiblioteca.exception.LibroNoEncontradoException;

@SpringBootTest
@Transactional
class LibroServiceTest {

    @Autowired
    private LibroService libroService;

    @Autowired
    private CategoriaService categoriaService;

    @Test
    void crearLibroConCategoria() {
        Long categoriaId = crearCategoria("Novela");

        LibroResponseDTO creado = libroService.crear(libro("Cien anos de soledad", "9780000000101", categoriaId));

        assertEquals("Cien anos de soledad", creado.getTitulo());
        assertEquals(categoriaId, creado.getCategoriaId());
        assertEquals("Novela", creado.getNombreCategoria());
        assertTrue(creado.getActivo());
    }

    @Test
    void crearLibroSinCategoria() {
        LibroResponseDTO creado = libroService.crear(libro("Rayuela", "9780000000102", null));

        assertNull(creado.getCategoriaId());
        assertEquals(1, libroService.listarActivos().size());
    }

    @Test
    void noPermiteIsbnDuplicado() {
        libroService.crear(libro("Ficciones", "9780000000103", null));

        assertThrows(IsbnDuplicadoException.class,
                () -> libroService.crear(libro("Otro titulo", "9780000000103", null)));
    }

    @Test
    void actualizarConservandoSuPropioIsbn() {
        LibroResponseDTO creado = libroService.crear(libro("Pedro Paramo", "9780000000104", null));

        LibroRequestDTO cambios = libro("Pedro Paramo (edicion revisada)", "9780000000104", null);
        cambios.setStock(10);
        LibroResponseDTO actualizado = libroService.actualizar(creado.getId(), cambios);

        assertEquals("Pedro Paramo (edicion revisada)", actualizado.getTitulo());
        assertEquals(10, actualizado.getStock());
    }

    @Test
    void noPermiteActualizarConIsbnDeOtroLibro() {
        libroService.crear(libro("Libro A", "9780000000105", null));
        LibroResponseDTO libroB = libroService.crear(libro("Libro B", "9780000000106", null));

        assertThrows(IsbnDuplicadoException.class,
                () -> libroService.actualizar(libroB.getId(), libro("Libro B", "9780000000105", null)));
    }

    @Test
    void rechazaCategoriaInexistente() {
        assertThrows(CategoriaNoEncontradaException.class,
                () -> libroService.crear(libro("Sin categoria valida", "9780000000107", 9999L)));
    }

    @Test
    void darDeBajaOcultaElLibroDelListado() {
        LibroResponseDTO creado = libroService.crear(libro("La ciudad y los perros", "9780000000108", null));

        libroService.darDeBaja(creado.getId());

        assertTrue(libroService.listarActivos().isEmpty());
        assertFalse(libroService.obtenerPorId(creado.getId()).getActivo());
    }

    @Test
    void obtenerLibroInexistenteLanzaExcepcion() {
        assertThrows(LibroNoEncontradoException.class, () -> libroService.obtenerPorId(9999L));
    }

    private Long crearCategoria(String nombre) {
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre(nombre);
        return categoriaService.crear(request).getId();
    }

    private LibroRequestDTO libro(String titulo, String isbn, Long categoriaId) {
        LibroRequestDTO request = new LibroRequestDTO();
        request.setTitulo(titulo);
        request.setAutor("Autor de prueba");
        request.setIsbn(isbn);
        request.setCategoriaId(categoriaId);
        request.setStock(3);
        return request;
    }
}
