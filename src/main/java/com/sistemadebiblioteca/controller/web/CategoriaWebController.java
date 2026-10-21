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

import com.sistemadebiblioteca.dto.CategoriaRequestDTO;
import com.sistemadebiblioteca.dto.CategoriaResponseDTO;
import com.sistemadebiblioteca.exception.CategoriaNoEncontradaException;
import com.sistemadebiblioteca.exception.OperacionNoPermitidaException;
import com.sistemadebiblioteca.exception.RecursoDuplicadoException;
import com.sistemadebiblioteca.service.CategoriaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/categorias")
public class CategoriaWebController {

    private final CategoriaService categoriaService;

    public CategoriaWebController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarActivas());
        return "categorias/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("categoria", categoriaService.obtenerPorId(id));
            model.addAttribute("libros", categoriaService.listarLibros(id));
            return "categorias/detalle";
        } catch (CategoriaNoEncontradaException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/categorias";
        }
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        return mostrarFormulario(model, new CategoriaRequestDTO(), null);
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("categoria") CategoriaRequestDTO categoria, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return mostrarFormulario(model, categoria, null);
        }
        try {
            categoriaService.crear(categoria);
        } catch (RecursoDuplicadoException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormulario(model, categoria, null);
        }
        redirect.addFlashAttribute("exito", "Categoría registrada correctamente");
        return "redirect:/categorias";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            CategoriaResponseDTO actual = categoriaService.obtenerPorId(id);
            CategoriaRequestDTO categoria = new CategoriaRequestDTO();
            categoria.setNombre(actual.getNombre());
            categoria.setDescripcion(actual.getDescripcion());
            return mostrarFormulario(model, categoria, id);
        } catch (CategoriaNoEncontradaException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/categorias";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("categoria") CategoriaRequestDTO categoria,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return mostrarFormulario(model, categoria, id);
        }
        try {
            categoriaService.actualizar(id, categoria);
        } catch (RecursoDuplicadoException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormulario(model, categoria, id);
        } catch (CategoriaNoEncontradaException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/categorias";
        }
        redirect.addFlashAttribute("exito", "Categoría actualizada correctamente");
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            categoriaService.darDeBaja(id);
            redirect.addFlashAttribute("exito", "Categoría dada de baja");
        } catch (CategoriaNoEncontradaException | OperacionNoPermitidaException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }

    private String mostrarFormulario(Model model, CategoriaRequestDTO categoria, Long id) {
        model.addAttribute("categoria", categoria);
        model.addAttribute("categoriaId", id);
        return "categorias/formulario";
    }
}
