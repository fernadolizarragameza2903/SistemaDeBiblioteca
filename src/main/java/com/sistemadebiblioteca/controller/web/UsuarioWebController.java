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

import com.sistemadebiblioteca.dto.UsuarioRequestDTO;
import com.sistemadebiblioteca.exception.RecursoDuplicadoException;
import com.sistemadebiblioteca.exception.RecursoNoEncontradoException;
import com.sistemadebiblioteca.repository.RolRepository;
import com.sistemadebiblioteca.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioWebController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    public UsuarioWebController(UsuarioService usuarioService, RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuariosActivos());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        return mostrarFormulario(model, new UsuarioRequestDTO());
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("usuario") UsuarioRequestDTO usuario, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return mostrarFormulario(model, usuario);
        }
        try {
            usuarioService.crearUsuario(usuario);
        } catch (RecursoDuplicadoException | RecursoNoEncontradoException e) {
            model.addAttribute("error", e.getMessage());
            return mostrarFormulario(model, usuario);
        }
        redirect.addFlashAttribute("exito", "Usuario registrado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            usuarioService.eliminarUsuario(id);
            redirect.addFlashAttribute("exito", "Usuario dado de baja");
        } catch (RecursoNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    private String mostrarFormulario(Model model, UsuarioRequestDTO usuario) {
        // La contraseña nunca se devuelve al formulario
        usuario.setPassword(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolRepository.findAll());
        return "usuarios/formulario";
    }
}
