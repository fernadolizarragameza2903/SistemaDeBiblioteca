package com.sistemadebiblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadebiblioteca.dto.CategoriaRequestDTO;
import com.sistemadebiblioteca.dto.CategoriaResponseDTO;
import com.sistemadebiblioteca.dto.LibroRequestDTO;
import com.sistemadebiblioteca.exception.OperacionNoPermitidaException;
import com.sistemadebiblioteca.exception.RecursoDuplicadoException;

@SpringBootTest
@Transactional
class CategoriaServiceTest {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private LibroService libroService;

    @Test
    void crearYListarCategoria() {
        CategoriaResponseDTO creada = categoriaService.crear(categoria("Poesia"));

        assertEquals("Poesia", creada.getNombre());
        assertEquals(1, categoriaService.listarActivas().size());
    }

    @Test
    void noPermiteNombresDuplicadosSinImportarMayusculas() {
        categoriaService.crear(categoria("Poesia"));

        assertThrows(RecursoDuplicadoException.class, () -> categoriaService.crear(categoria("POESIA")));
    }

    @Test
    void noPermiteDarDeBajaCategoriaConLibrosActivos() {
        CategoriaResponseDTO creada = categoriaService.crear(categoria("Ensayo"));
        LibroRequestDTO libro = new LibroRequestDTO();
        libro.setTitulo("El laberinto de la soledad");
        libro.setAutor("Octavio Paz");
        libro.setIsbn("9780000000300");
        libro.setCategoriaId(creada.getId());
        libro.setStock(1);
        libroService.crear(libro);

        assertThrows(OperacionNoPermitidaException.class, () -> categoriaService.darDeBaja(creada.getId()));
        assertEquals(1, categoriaService.listarLibros(creada.getId()).size());
    }

    private CategoriaRequestDTO categoria(String nombre) {
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre(nombre);
        return request;
    }
}
