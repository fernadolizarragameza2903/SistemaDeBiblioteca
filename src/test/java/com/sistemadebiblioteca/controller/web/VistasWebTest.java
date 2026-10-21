package com.sistemadebiblioteca.controller.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.sistemadebiblioteca.model.Categoria;
import com.sistemadebiblioteca.model.Libro;
import com.sistemadebiblioteca.model.Rol;
import com.sistemadebiblioteca.model.Usuario;
import com.sistemadebiblioteca.repository.CategoriaRepository;
import com.sistemadebiblioteca.repository.LibroRepository;
import com.sistemadebiblioteca.repository.RolRepository;
import com.sistemadebiblioteca.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VistasWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LibroRepository libroRepository;

    private Usuario usuario;
    private Libro libro;
    private Categoria categoria;

    @BeforeEach
    void prepararDatos() {
        Rol rol = new Rol();
        rol.setNombre("LECTOR");
        rol = rolRepository.save(rol);

        usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellido("Torres");
        usuario.setEmail("ana.web@correo.com");
        usuario.setPasswordHash("hash");
        usuario.setRol(rol);
        usuario = usuarioRepository.save(usuario);

        categoria = new Categoria();
        categoria.setNombre("Novela");
        categoria = categoriaRepository.save(categoria);

        libro = new Libro();
        libro.setTitulo("Rayuela");
        libro.setAutor("Julio Cortazar");
        libro.setIsbn("9780000000500");
        libro.setCategoria(categoria);
        libro.setStock(2);
        libro = libroRepository.save(libro);
    }

    @Test
    void todasLasVistasRenderizan() throws Exception {
        String[] rutas = {
                "/", "/libros", "/libros?q=rayuela", "/libros/nuevo", "/libros/" + libro.getId() + "/editar",
                "/categorias", "/categorias/nueva", "/categorias/" + categoria.getId(),
                "/categorias/" + categoria.getId() + "/editar",
                "/usuarios", "/usuarios/nuevo",
                "/prestamos", "/prestamos/vencidos", "/prestamos/nuevo", "/prestamos/usuario/" + usuario.getId()
        };
        for (String ruta : rutas) {
            mockMvc.perform(get(ruta))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("bootstrap")));
        }
    }

    @Test
    void registrarYDevolverPrestamoDesdeLaWeb() throws Exception {
        mockMvc.perform(post("/prestamos")
                        .param("usuarioId", usuario.getId().toString())
                        .param("libroId", libro.getId().toString()))
                .andExpect(redirectedUrl("/prestamos"))
                .andExpect(flash().attributeExists("exito"));

        mockMvc.perform(get("/prestamos"))
                .andExpect(content().string(containsString("Rayuela")))
                .andExpect(content().string(containsString("Devolver")));
    }

    @Test
    void formularioDeLibroMuestraErroresDeValidacion() throws Exception {
        mockMvc.perform(post("/libros").param("titulo", "").param("autor", "").param("isbn", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("libro", "titulo", "autor", "isbn", "stock"))
                .andExpect(content().string(containsString("El titulo es obligatorio")));
    }
}
