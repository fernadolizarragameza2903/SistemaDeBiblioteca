-- =====================================================================
-- Sistema de Biblioteca - Datos iniciales
-- Ejecutar despues de 01_schema.sql:
--   psql -U postgres -d sistema_biblioteca -f database/02_data.sql
-- Contraseña de todos los usuarios de prueba: Biblioteca123
-- =====================================================================

INSERT INTO roles (nombre) VALUES
    ('ADMIN'),
    ('BIBLIOTECARIO'),
    ('LECTOR');

INSERT INTO usuarios (nombre, apellido, email, password_hash, rol_id) VALUES
    ('Admin',  'Sistema',  'admin@biblioteca.com',
        '$2a$10$ODA/Rc7hACPuV2EPopLJ2.m04PhW4oGy.XrdKBeh51yjzMlEaONli', 1),
    ('Maria',  'Quispe',   'maria.quispe@biblioteca.com',
        '$2a$10$ODA/Rc7hACPuV2EPopLJ2.m04PhW4oGy.XrdKBeh51yjzMlEaONli', 2),
    ('Carlos', 'Ramirez',  'carlos.ramirez@correo.com',
        '$2a$10$ODA/Rc7hACPuV2EPopLJ2.m04PhW4oGy.XrdKBeh51yjzMlEaONli', 3),
    ('Lucia',  'Fernandez','lucia.fernandez@correo.com',
        '$2a$10$ODA/Rc7hACPuV2EPopLJ2.m04PhW4oGy.XrdKBeh51yjzMlEaONli', 3);

INSERT INTO categorias (nombre, descripcion) VALUES
    ('Novela',      'Obras de ficcion narrativa'),
    ('Ciencia',     'Divulgacion y textos cientificos'),
    ('Historia',    'Historia universal y del Peru'),
    ('Tecnologia',  'Programacion, redes y sistemas'),
    ('Infantil',    'Libros para ninos y jovenes');

INSERT INTO libros (titulo, autor, isbn, categoria_id, stock) VALUES
    ('Cien anos de soledad',          'Gabriel Garcia Marquez', '9780307474728', 1, 5),
    ('La ciudad y los perros',        'Mario Vargas Llosa',     '9788420412146', 1, 3),
    ('Los rios profundos',            'Jose Maria Arguedas',    '9788437604183', 1, 2),
    ('Breve historia del tiempo',     'Stephen Hawking',        '9780553380163', 2, 4),
    ('Cosmos',                        'Carl Sagan',             '9780345539434', 2, 2),
    ('Historia de la Republica del Peru', 'Jorge Basadre',      '9786124147163', 3, 1),
    ('Clean Code',                    'Robert C. Martin',       '9780132350884', 4, 3),
    ('Spring Boot in Action',         'Craig Walls',            '9781617292545', 4, 2),
    ('El principito',                 'Antoine de Saint-Exupery','9780156012195', 5, 6),
    ('Paco Yunque',                   'Cesar Vallejo',          '9786123050471', 5, 0);

-- Un prestamo activo y uno ya devuelto para tener datos de ejemplo
INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, estado) VALUES
    (3, 1, CURRENT_TIMESTAMP, CURRENT_DATE + 7, 'ACTIVO');
UPDATE libros SET stock = stock - 1 WHERE id = 1;

INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada,
                       fecha_devolucion_real, estado) VALUES
    (4, 7, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_DATE - 3,
     CURRENT_TIMESTAMP - INTERVAL '4 days', 'DEVUELTO');
