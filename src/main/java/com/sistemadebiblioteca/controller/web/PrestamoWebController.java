package com.sistemadebiblioteca.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistemadebiblioteca.dto.PrestamoRequestDTO;
import com.sistemadebiblioteca.dto.UsuarioResponseDTO;
import com.sistemadebiblioteca.exception.LibroNoEncontradoException;
import com.sistemadebiblioteca.exception.OperacionNoPermitidaException;
import com.sistemadebiblioteca.exception.RecursoNoEncontradoException;
import com.sistemadebiblioteca.service.LibroService;
import com.sistemadebiblioteca.service.PrestamoService;
import com.sistemadebiblioteca.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/prestamos")
public class PrestamoWebController {

    private final PrestamoService prestamoService;
    private final LibroService libroService;
    private final UsuarioService usuarioService;

    public PrestamoWebController(PrestamoService prestamoService, LibroService libroService,
            UsuarioService usuarioService) {
        this.prestamoService = prestamoService;
        this.libroService = libroService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarActivos(Model model) {
        model.addAttribute("prestamos", prestamoService.listarActivos());
        model.addAttribute("vista", "activos");
        model.addAttribute("titulo", "Préstamos activos");
        return "prestamos/lista";
    }

    @GetMapping("/vencidos")
    public String listarVencidos(Model model) {
        model.addAttribute("prestamos", prestamoService.listarVencidos());
        model.addAttribute("vista", "vencidos");
        model.addAttribute("titulo", "Préstamos vencidos");
        return "prestamos/lista";
    }

    @GetMapping("/usuario/{usuarioId}")
    public String historialUsuario(@PathVariable Long usuarioId, Model model, RedirectAttributes redirect) {
        try {
            UsuarioResponseDTO usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
            model.addAttribute("prestamos", prestamoService.listarPorUsuario(usuarioId));
            model.addAttribute("vista", "historial");
            model.addAttribute("titulo", "Historial de " + usuario.getNombre() + " " + usuario.getApellido());
            return "prestamos/lista";
        } catch (RecursoNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios";
        }
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        return mostrarFormulario(model, new PrestamoRequestDTO());
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("prestamo") PrestamoRequestDTO prestamo, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return mostrarFormulario(model, prestamo);
        }
        try {
            prestamoService.registrar(prestamo);
        } catch (RecursoNoEncontradoException | LibroNoEncontradoException | OperacionNoPermitidaException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormulario(model, prestamo);
        }
        redirect.addFlashAttribute("exito", "Préstamo registrado correctamente");
        return "redirect:/prestamos";
    }

    @PostMapping("/{id}/devolucion")
    public String devolver(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            prestamoService.devolver(id);
            redirect.addFlashAttribute("exito", "Devolución registrada; el stock fue repuesto");
        } catch (RecursoNoEncontradoException | OperacionNoPermitidaException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos";
    }

    private String mostrarFormulario(Model model, PrestamoRequestDTO prestamo) {
        model.addAttribute("prestamo", prestamo);
        model.addAttribute("usuarios", usuarioService.listarUsuariosActivos());
        model.addAttribute("libros", libroService.listarActivos()
                .stream()
                .filter(libro -> libro.getStock() > 0)
                .toList());
        return "prestamos/formulario";
    }
}
