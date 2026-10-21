package com.sistemadebiblioteca.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadebiblioteca.dto.PrestamoRequestDTO;
import com.sistemadebiblioteca.dto.PrestamoResponseDTO;
import com.sistemadebiblioteca.exception.LibroNoEncontradoException;
import com.sistemadebiblioteca.exception.OperacionNoPermitidaException;
import com.sistemadebiblioteca.exception.RecursoNoEncontradoException;
import com.sistemadebiblioteca.model.EstadoPrestamo;
import com.sistemadebiblioteca.model.Libro;
import com.sistemadebiblioteca.model.Prestamo;
import com.sistemadebiblioteca.model.Usuario;
import com.sistemadebiblioteca.repository.LibroRepository;
import com.sistemadebiblioteca.repository.PrestamoRepository;
import com.sistemadebiblioteca.repository.UsuarioRepository;
import com.sistemadebiblioteca.service.PrestamoService;

@Service
@Transactional
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final int diasMaximos;
    private final int maximoActivosPorUsuario;

    public PrestamoServiceImpl(PrestamoRepository prestamoRepository, LibroRepository libroRepository,
            UsuarioRepository usuarioRepository,
            @Value("${app.prestamos.dias-maximos:14}") int diasMaximos,
            @Value("${app.prestamos.maximo-activos-por-usuario:3}") int maximoActivosPorUsuario) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
        this.diasMaximos = diasMaximos;
        this.maximoActivosPorUsuario = maximoActivosPorUsuario;
    }

    @Override
    public PrestamoResponseDTO registrar(PrestamoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .filter(Usuario::getActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontro un usuario activo con id " + request.getUsuarioId()));

        Libro libro = libroRepository.findById(request.getLibroId())
                .filter(Libro::getActivo)
                .orElseThrow(() -> new LibroNoEncontradoException(
                        "No se encontro un libro activo con id " + request.getLibroId()));

        validarReglasDePrestamo(usuario, libro);

        int dias = request.getDias() != null ? request.getDias() : diasMaximos;
        libro.setStock(libro.getStock() - 1);

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDateTime.now());
        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(dias));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        prestamo.setObservaciones(request.getObservaciones());

        libroRepository.save(libro);
        return convertirAResponse(prestamoRepository.save(prestamo));
    }

    @Override
    public PrestamoResponseDTO devolver(Long id) {
        Prestamo prestamo = buscarPrestamoPorId(id);
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new OperacionNoPermitidaException("El prestamo con id " + id + " ya fue devuelto");
        }

        Libro libro = prestamo.getLibro();
        libro.setStock(libro.getStock() + 1);
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucionReal(LocalDateTime.now());

        libroRepository.save(libro);
        return convertirAResponse(prestamoRepository.save(prestamo));
    }

    @Override
    @Transactional(readOnly = true)
    public PrestamoResponseDTO obtenerPorId(Long id) {
        return convertirAResponse(buscarPrestamoPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> listarActivos() {
        return prestamoRepository.findByEstadoOrderByFechaPrestamoDesc(EstadoPrestamo.ACTIVO)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> listarVencidos() {
        return prestamoRepository
                .findByEstadoAndFechaDevolucionEsperadaBefore(EstadoPrestamo.ACTIVO, LocalDate.now())
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> listarPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException("No se encontro el usuario con id " + usuarioId);
        }
        return prestamoRepository.findByUsuarioIdOrderByFechaPrestamoDesc(usuarioId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private void validarReglasDePrestamo(Usuario usuario, Libro libro) {
        if (libro.getStock() <= 0) {
            throw new OperacionNoPermitidaException("El libro '" + libro.getTitulo() + "' no tiene stock disponible");
        }
        long activos = prestamoRepository.countByUsuarioIdAndEstado(usuario.getId(), EstadoPrestamo.ACTIVO);
        if (activos >= maximoActivosPorUsuario) {
            throw new OperacionNoPermitidaException("El usuario ya tiene " + activos
                    + " prestamos activos (maximo permitido: " + maximoActivosPorUsuario + ")");
        }
        if (prestamoRepository.existsByUsuarioIdAndLibroIdAndEstado(usuario.getId(), libro.getId(),
                EstadoPrestamo.ACTIVO)) {
            throw new OperacionNoPermitidaException("El usuario ya tiene prestado este libro");
        }
    }

    private Prestamo buscarPrestamoPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el prestamo con id " + id));
    }

    private PrestamoResponseDTO convertirAResponse(Prestamo prestamo) {
        PrestamoResponseDTO response = new PrestamoResponseDTO();
        response.setId(prestamo.getId());
        response.setUsuarioId(prestamo.getUsuario().getId());
        response.setNombreUsuario(prestamo.getUsuario().getNombre() + " " + prestamo.getUsuario().getApellido());
        response.setLibroId(prestamo.getLibro().getId());
        response.setTituloLibro(prestamo.getLibro().getTitulo());
        response.setFechaPrestamo(prestamo.getFechaPrestamo());
        response.setFechaDevolucionEsperada(prestamo.getFechaDevolucionEsperada());
        response.setFechaDevolucionReal(prestamo.getFechaDevolucionReal());
        response.setEstado(prestamo.getEstado().name());
        response.setVencido(prestamo.estaVencido());
        response.setObservaciones(prestamo.getObservaciones());
        return response;
    }
}
