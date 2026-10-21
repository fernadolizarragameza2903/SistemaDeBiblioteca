package com.sistemadebiblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadebiblioteca.dto.PrestamoRequestDTO;
import com.sistemadebiblioteca.dto.PrestamoResponseDTO;
import com.sistemadebiblioteca.exception.OperacionNoPermitidaException;
import com.sistemadebiblioteca.model.Libro;
import com.sistemadebiblioteca.model.Rol;
import com.sistemadebiblioteca.model.Usuario;
import com.sistemadebiblioteca.repository.LibroRepository;
import com.sistemadebiblioteca.repository.RolRepository;
import com.sistemadebiblioteca.repository.UsuarioRepository;

@SpringBootTest
@Transactional
class PrestamoServiceTest {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    private Usuario lector;

    @BeforeEach
    void prepararDatos() {
        Rol rol = new Rol();
        rol.setNombre("LECTOR");
        rol = rolRepository.save(rol);

        lector = new Usuario();
        lector.setNombre("Ana");
        lector.setApellido("Torres");
        lector.setEmail("ana.torres@correo.com");
        lector.setPasswordHash("hash");
        lector.setRol(rol);
        lector = usuarioRepository.save(lector);
    }

    @Test
    void registrarPrestamoDescuentaStock() {
        Libro libro = crearLibro("9780000000001", 2);

        PrestamoResponseDTO prestamo = prestamoService.registrar(solicitud(libro.getId()));

        assertEquals("ACTIVO", prestamo.getEstado());
        assertFalse(prestamo.getVencido());
        assertEquals(1, libroRepository.findById(libro.getId()).orElseThrow().getStock());
    }

    @Test
    void devolverPrestamoReponeStock() {
        Libro libro = crearLibro("9780000000002", 1);
        PrestamoResponseDTO prestamo = prestamoService.registrar(solicitud(libro.getId()));

        PrestamoResponseDTO devuelto = prestamoService.devolver(prestamo.getId());

        assertEquals("DEVUELTO", devuelto.getEstado());
        assertEquals(1, libroRepository.findById(libro.getId()).orElseThrow().getStock());
    }

    @Test
    void noPermitePrestarLibroSinStock() {
        Libro libro = crearLibro("9780000000003", 0);

        assertThrows(OperacionNoPermitidaException.class,
                () -> prestamoService.registrar(solicitud(libro.getId())));
    }

    @Test
    void noPermiteDevolverDosVeces() {
        Libro libro = crearLibro("9780000000004", 1);
        PrestamoResponseDTO prestamo = prestamoService.registrar(solicitud(libro.getId()));
        prestamoService.devolver(prestamo.getId());

        assertThrows(OperacionNoPermitidaException.class, () -> prestamoService.devolver(prestamo.getId()));
    }

    @Test
    void respetaMaximoDePrestamosActivosPorUsuario() {
        for (int i = 0; i < 3; i++) {
            Libro libro = crearLibro("978000000010" + i, 1);
            prestamoService.registrar(solicitud(libro.getId()));
        }
        Libro extra = crearLibro("9780000000200", 1);

        assertThrows(OperacionNoPermitidaException.class,
                () -> prestamoService.registrar(solicitud(extra.getId())));
    }

    private PrestamoRequestDTO solicitud(Long libroId) {
        PrestamoRequestDTO request = new PrestamoRequestDTO();
        request.setUsuarioId(lector.getId());
        request.setLibroId(libroId);
        return request;
    }

    private Libro crearLibro(String isbn, int stock) {
        Libro libro = new Libro();
        libro.setTitulo("Libro " + isbn);
        libro.setAutor("Autor de prueba");
        libro.setIsbn(isbn);
        libro.setStock(stock);
        return libroRepository.save(libro);
    }
}
