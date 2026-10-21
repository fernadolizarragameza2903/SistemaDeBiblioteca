package com.sistemadebiblioteca.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistemadebiblioteca.dto.LibroRequestDTO;
import com.sistemadebiblioteca.dto.LibroResponseDTO;
import com.sistemadebiblioteca.exception.CategoriaNoEncontradaException;
import com.sistemadebiblioteca.exception.IsbnDuplicadoException;
import com.sistemadebiblioteca.exception.LibroNoEncontradoException;
import com.sistemadebiblioteca.service.CategoriaService;
import com.sistemadebiblioteca.service.LibroService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/libros")
public class LibroWebController {

    private final LibroService libroService;
    private final CategoriaService categoriaService;

    public LibroWebController(LibroService libroService, CategoriaService categoriaService) {
        this.libroService = libroService;
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        List<LibroResponseDTO> libros = libroService.listarActivos();
        if (q != null && !q.isBlank()) {
            String filtro = q.trim().toLowerCase();
            libros = libros.stream()
                    .filter(libro -> libro.getTitulo().toLowerCase().contains(filtro)
                            || libro.getAutor().toLowerCase().contains(filtro)
                            || libro.getIsbn().contains(filtro))
                    .toList();
        }
        model.addAttribute("libros", libros);
        model.addAttribute("q", q);
        return "libros/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        LibroRequestDTO libro = new LibroRequestDTO();
        libro.setStock(1);
        return mostrarFormulario(model, libro, null);
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("libro") LibroRequestDTO libro, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return mostrarFormulario(model, libro, null);
        }
        try {
            libroService.crear(libro);
        } catch (IsbnDuplicadoException | CategoriaNoEncontradaException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormulario(model, libro, null);
        }
        redirect.addFlashAttribute("exito", "Libro registrado correctamente");
        return "redirect:/libros";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            LibroResponseDTO actual = libroService.obtenerPorId(id);
            LibroRequestDTO libro = new LibroRequestDTO();
            libro.setTitulo(actual.getTitulo());
            libro.setAutor(actual.getAutor());
            libro.setIsbn(actual.getIsbn());
            libro.setCategoriaId(actual.getCategoriaId());
            libro.setStock(actual.getStock());
            return mostrarFormulario(model, libro, id);
        } catch (LibroNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/libros";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("libro") LibroRequestDTO libro,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return mostrarFormulario(model, libro, id);
        }
        try {
            libroService.actualizar(id, libro);
        } catch (IsbnDuplicadoException | CategoriaNoEncontradaException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormulario(model, libro, id);
        } catch (LibroNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/libros";
        }
        redirect.addFlashAttribute("exito", "Libro actualizado correctamente");
        return "redirect:/libros";
    }

    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            libroService.darDeBaja(id);
            redirect.addFlashAttribute("exito", "Libro dado de baja");
        } catch (LibroNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/libros";
    }

    private String mostrarFormulario(Model model, LibroRequestDTO libro, Long id) {
        model.addAttribute("libro", libro);
        model.addAttribute("libroId", id);
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "libros/formulario";
    }
}
