package com.sistemadebiblioteca.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sistemadebiblioteca.service.CategoriaService;
import com.sistemadebiblioteca.service.LibroService;
import com.sistemadebiblioteca.service.PrestamoService;
import com.sistemadebiblioteca.service.UsuarioService;

@Controller
public class InicioWebController {

    private final LibroService libroService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final PrestamoService prestamoService;

    public InicioWebController(LibroService libroService, CategoriaService categoriaService,
            UsuarioService usuarioService, PrestamoService prestamoService) {
        this.libroService = libroService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.prestamoService = prestamoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalLibros", libroService.listarActivos().size());
        model.addAttribute("totalCategorias", categoriaService.listarActivas().size());
        model.addAttribute("totalUsuarios", usuarioService.listarUsuariosActivos().size());
        model.addAttribute("prestamosActivos", prestamoService.listarActivos());
        model.addAttribute("totalVencidos", prestamoService.listarVencidos().size());
        return "index";
    }
}
