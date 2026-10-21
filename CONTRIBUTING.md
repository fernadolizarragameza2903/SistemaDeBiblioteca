# Guía de colaboración del equipo

Reglas de trabajo con Git y GitHub para el proyecto **Sistema de Biblioteca**. Todo integrante debe seguirlas; el revisor de un Pull Request las usa como checklist.

## 1. Flujo de trabajo (GitHub Flow)

```text
Issue en el tablero  →  rama desde main  →  commits  →  push  →  Pull Request  →  Code Review  →  merge a main  →  tag / Release
```

1. **Nunca** se hace commit directo sobre `main`. `main` siempre debe compilar y pasar las pruebas.
2. Toda tarea nace como un **Issue** en el tablero (GitHub Projects) con responsable, prioridad y estado.
3. Antes de empezar, actualiza `main` y crea tu rama:

   ```bash
   git switch main
   git pull origin main
   git switch -c feature/12-gestion-prestamos
   ```

4. Trabaja en commits pequeños, revisa el diff y publica la rama:

   ```bash
   git status
   git diff
   git add <archivos>
   git commit -m "feat(prestamos): registrar devolucion de libros"
   git push -u origin feature/12-gestion-prestamos
   ```

5. Abre un **Pull Request** hacia `main` usando la plantilla, enlaza el Issue con `Closes #12` y asigna a **otro integrante** como revisor.
6. El revisor comenta, solicita cambios o aprueba. Solo se hace merge con al menos **1 aprobación**.
7. Después del merge, borra la rama remota y actualiza tu `main` local.

## 2. Nombres de ramas

Formato: `tipo/numeroIssue-descripcion-corta` (minúsculas y guiones; el número de issue es opcional pero recomendado).

| Prefijo      | Uso                                          | Ejemplo                               |
|--------------|----------------------------------------------|---------------------------------------|
| `feature/`   | Nueva funcionalidad                          | `feature/15-gestion-reservas`         |
| `fix/`       | Corrección de un error                       | `fix/21-stock-negativo`               |
| `docs/`      | Solo documentación                           | `docs/guia-colaboracion`              |
| `refactor/`  | Reorganizar código sin cambiar comportamiento| `refactor/normalizar-paquetes`        |
| `test/`      | Agregar o corregir pruebas                   | `test/pruebas-servicios`              |
| `chore/`     | Configuración, dependencias, build           | `chore/actualizar-spring-boot`        |
| `hotfix/`    | Corrección urgente sobre una versión publicada | `hotfix/v0.2.1-login`               |

## 3. Mensajes de commit (Conventional Commits)

Formato:

```text
tipo(alcance opcional): descripción en imperativo y minúsculas

Cuerpo opcional: qué cambia y por qué.

Closes #12
```

| Tipo       | Cuándo usarlo                                             |
|------------|-----------------------------------------------------------|
| `feat`     | Nueva funcionalidad para el usuario                       |
| `fix`      | Corrección de un error                                    |
| `docs`     | Cambios solo en documentación (README, guías)             |
| `style`    | Formato, espacios, punto y coma (sin cambiar lógica)      |
| `refactor` | Reestructurar código sin cambiar su comportamiento        |
| `test`     | Agregar o modificar pruebas                               |
| `chore`    | Tareas de mantenimiento: build, dependencias, configuración |
| `perf`     | Mejora de rendimiento                                     |

Alcances usados en el proyecto: `libros`, `categorias`, `usuarios`, `prestamos`, `db`, `config`.

**Buenos ejemplos**

```text
feat(prestamos): validar maximo de prestamos activos por usuario
fix(usuarios): devolver 409 cuando el email ya esta registrado
docs: documentar endpoints de categorias en el README
```

**Malos ejemplos** (no usar): `cambios`, `update`, `se arreglo todo`, `asdf`.

## 4. Pull Requests

- Título con el mismo formato de commit: `feat(prestamos): registro y devolucion de libros`.
- Completa la plantilla: qué se hizo, cómo probarlo, Issue relacionado.
- Un PR = un objetivo. Si el PR toca muchos módulos, divídelo.
- Antes de pedir revisión, actualiza tu rama con `main` y corre las pruebas:

  ```bash
  git pull origin main
  .\mvnw.cmd test
  ```

### Checklist del revisor (Code Review)

- [ ] El código compila y `mvnw test` pasa.
- [ ] El cambio hace lo que dice el Issue y nada más.
- [ ] Nombres claros, sin código comentado ni archivos basura (`target/`, `.idea/`).
- [ ] Validaciones de entrada (`@Valid`) y manejo de errores con excepciones del dominio.
- [ ] No hay contraseñas ni datos sensibles en el código.
- [ ] Si cambia la base de datos, se actualizó `database/01_schema.sql`.
- [ ] Los commits siguen la convención.

El revisor deja al menos un comentario concreto y luego **Approve** o **Request changes**.

## 5. Resolución de conflictos

Cuando Git no puede combinar dos cambios sobre las mismas líneas, marca el archivo así:

```text
<<<<<<< HEAD
versión de la rama actual (la que recibe el merge)
=======
versión de la rama que se está integrando
>>>>>>> feature/otra-rama
```

Pasos:

1. `git status` para ver los archivos en conflicto.
2. Editar el archivo, decidir la versión correcta (o combinar ambas) y borrar los marcadores.
3. Verificar que no quedan marcadores: `git diff --check` o `git grep -n "<<<<<<<"`.
4. `git add <archivo>` y `git commit` para cerrar el merge.
5. Compilar y correr pruebas antes de hacer push.

Git **no decide** qué versión es la correcta: eso lo decide el equipo.

## 6. Versiones, tags y Releases (SemVer)

Usamos [Versionado Semántico](https://semver.org/lang/es/) `vMAYOR.MENOR.PARCHE`:

- **MAYOR**: cambios incompatibles (en la etapa 0.x aún no hay versión estable).
- **MENOR**: nuevas funcionalidades compatibles (ej. `v0.2.0` agrega préstamos y categorías).
- **PARCHE**: solo correcciones (ej. `v0.2.1`).

Crear una versión (solo desde `main` actualizado y con pruebas pasando):

```bash
git switch main
git pull origin main
git tag -a v0.2.0 -m "v0.2.0: gestion de categorias, prestamos y base de datos"
git push origin v0.2.0
```

Luego en GitHub: **Releases → Draft a new release → elegir el tag** y pegar la sección correspondiente del [CHANGELOG](CHANGELOG.md).

## 7. Tablero de gestión (GitHub Projects)

| Estado        | Criterio para estar ahí                                               |
|---------------|-----------------------------------------------------------------------|
| **Backlog**   | Tarea identificada, aún no priorizada para la semana                  |
| **Pendiente** | Priorizada y con responsable asignado                                 |
| **En progreso** | Existe una rama creada y hay commits                                |
| **En revisión** | Existe un Pull Request abierto con revisor asignado                 |
| **Bloqueada** | Depende de otra tarea; se indica cuál en un comentario                |
| **Finalizada** | El PR fue aprobado y fusionado en `main`                             |

Prioridades: **Alta** (bloquea a otros o es requisito de la entrega), **Media** (funcionalidad planificada), **Baja** (mejoras). Solo el responsable mueve su tarea, y solo a *Finalizada* cuando existe el merge que lo demuestra.
