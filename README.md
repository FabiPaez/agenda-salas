# Agenda de Salas

API REST para la gestión de reservas de salas de un cine, con validaciones de negocio, persistencia en MySQL, registro seguro de usuarios y autorización por roles mediante Spring Security y HTTP Basic Auth.

## 1. Información del proyecto

### Equipo de desarrollo
- Fabián Páez
- Facundo Santana
- Pablo Cortez

### Tecnologías utilizadas
- Java 21
- Spring Boot
- Maven
- Spring Web MVC
- Spring Security y HTTP Basic Auth
- Spring Data JPA e Hibernate
- MySQL
- Bean Validation
- BCrypt para el almacenamiento seguro de contraseñas
- Postman para las pruebas manuales

**Repositorio:** https://github.com/FabiPaez/agenda-salas

**Rama de entrega:** `main`

**Commit de entrega:** "Preparando Segundo Entregable".

### Despliegue

- **Backend:** https://agenda-salas.onrender.com
- **Base de datos:** MySQL en Aiven.

La aplicación utiliza variables de entorno para configurar la conexión a la base de datos. La disponibilidad del servicio publicado depende del proveedor de alojamiento; la primera petición puede demorar debido a la modalidad de alojamiento gratuito.

## 2. Descripción y objetivo

Agenda de Salas es una API REST para administrar usuarios, salas de cine y reservas asociadas a un usuario y una sala.

Permite registrar usuarios, consultar y administrar salas, crear y consultar reservas, modificar datos y eliminar recursos. Además, valida las relaciones entre entidades y evita la superposición de reservas confirmadas para una misma sala y fecha.

Corregimos las siguientes observaciones del Primer Entregable:

- En los controladores utilizamos `ResponseEntity` en lugar de `@ResponseStatus`.
- En `ApiExceptionHandler`, devolvemos un DTO de error, en lugar de un `Map<String, String>.
- Optimizamos `ReservaService` con las modificaciones correspondientes.

Para este Segundo Entregable incorporamos:

- Almacenamiento de contraseñas mediante BCrypt.
- Autenticación HTTP Basic con Spring Security.
- Autorización de operaciones según el rol del usuario.
- Respuestas diferenciadas para errores de autenticación y autorización.
- Protección de los datos de contraseña en las respuestas de la API.

Aclaración: Este proyecto no constituye un sistema completo de venta de entradas ni de gestión de películas y funciones.

## 3. Modelo de datos

El sistema cuenta con tres entidades principales: `Usuario`, `Sala` y `Reserva`.

### Usuario

Representa a una persona registrada en el sistema.

Atributos principales:
- `id`: identificador único.
- `nombre`: nombre del usuario.
- `apellido`: apellido del usuario.
- `email`: correo electrónico único, utilizado como nombre de usuario para Basic Auth.
- `contrasena`: contraseña almacenada como hash BCrypt.
- `tipo`: rol del usuario (`CLIENTE`, `PERSONAL` o `ADMIN`).

La contraseña original no se devuelve en los DTO de respuesta. El atributo `contrasena` contiene el hash almacenado, no la contraseña original.

### Sala

Representa una sala que puede ser reservada.

Atributos:
- `id`: identificador único.
- `numero`: número de sala, único.
- `capacidad`: capacidad de la sala.
- `estado`: `DISPONIBLE`, `MANTENIMIENTO` o `INACTIVA`.

### Reserva

Representa la reserva de una sala para una fecha y un intervalo horario.

Atributos:
- `id`: identificador único.
- `fecha`: fecha de la reserva.
- `horaInicio`: hora de inicio.
- `horaFin`: hora de finalización.
- `usuario`: usuario asociado.
- `sala`: sala reservada.
- `estado`: `CONFIRMADA` o `CANCELADA`.

### Relaciones JPA

- Un usuario puede tener cero o muchas reservas.
- Una sala puede tener cero o muchas reservas.
- Cada reserva pertenece a un usuario y a una sala.

Las relaciones se implementan mediante asociaciones `@ManyToOne` desde `Reserva` hacia `Usuario` y `Sala`, con sus respectivas claves foráneas.

La API incluye consultas de reservas por usuario y por sala, que permiten verificar el uso de estas relaciones.

## 4. Registro, autenticación y autorización

### 4.1. Registro público

El registro de usuarios se realiza mediante:

`POST /api/auth/registro`

Esta ruta es pública y no requiere credenciales.

Ejemplo de solicitud:

```http
POST /api/auth/registro
Content-Type: application/json
```

```json
{
  "nombre": "Mario",
  "apellido": "Ruiz",
  "email": "mario.prueba@ejemplo.com",
  "password": "ClaveDemo123"
}
```

La contraseña debe tener entre 6 y 50 caracteres. El correo debe tener un formato válido y no puede superar los 150 caracteres. El nombre y el apellido son obligatorios y tienen una longitud máxima de 80 caracteres.

El registro asigna automáticamente el rol `CLIENTE`. El cliente de la API no puede elegir `PERSONAL` ni `ADMIN` mediante esta solicitud.

Respuesta esperada: `201 Created`.

```json
{
  "id": 7,
  "nombre": "Mario",
  "apellido": "Ruiz",
  "email": "mario.prueba@ejemplo.com",
  "tipo": "CLIENTE"
}
```

El identificador dependerá de los registros existentes. La respuesta está configurada para no contener la contraseña original ni su hash.

Resultados adicionales:
- `400 Bad Request`: campos inválidos o faltantes.
- `409 Conflict`: el correo ya está registrado.

La comprobación de correo duplicado se realiza en el servicio y la base de datos mantiene una restricción de unicidad.

### 4.2. HTTP Basic Auth

Las rutas protegidas utilizan HTTP Basic Auth mediante Spring Security.

En Postman:

1. Abrir la pestaña **Authorization**.
2. Seleccionar **Basic Auth**.
3. En **Username**, ingresar el correo electrónico registrado.
4. En **Password**, ingresar la contraseña original.
5. Enviar la petición.

La aplicación busca al usuario por correo mediante `UserDetailsService` y compara la contraseña recibida con el hash BCrypt almacenado.

No debe ingresarse el hash como contraseña en Postman. Cada petición protegida debe incluir las credenciales.


### 4.3. Roles y permisos

La aplicación utiliza tres roles: `CLIENTE`, `PERSONAL` y `ADMIN`.

| Operación | CLIENTE | PERSONAL | ADMIN |
|---|---|---|---|
| Registrar una cuenta | Público | Público | Público |
| Consultar salas | Sí | Sí | Sí |
| Crear salas | No | No | Sí |
| Modificar salas | No | No | Sí |
| Eliminar salas | No | No | Sí |
| Consultar usuarios | No | No | Sí |
| Crear usuarios por `/api/usuarios` | No | No | Sí |
| Modificar usuarios | No | No | Sí |
| Eliminar usuarios | No | No | Sí |
| Consultar reservas | Sí | Sí | Sí |
| Crear reservas | Sí | Sí | Sí |
| Modificar reservas | Sí | Sí | Sí |
| Eliminar reservas | Sí | Sí | Sí |

Las operaciones de reservas requieren autenticación, pero no se restringen por rol en esta versión. En una próxima versión implementaremos una comprobación de propiedad de cada reserva para impedir que un cliente opere sobre reservas asociadas a otro usuario.

La configuración de seguridad establece las reglas de acceso mediante `SecurityFilterChain` y `hasRole(...)`.

### 4.4. Preparación de un administrador de prueba

El registro público no permite crear administradores directamente. Para preparar una cuenta administrativa de prueba:

1. Registrar un usuario mediante `POST /api/auth/registro`, utilizando una contraseña ficticia destinada exclusivamente a la evaluación.
2. En la base de datos, asignar el rol `ADMIN` al correo registrado.

Ejemplo SQL, que debe ejecutarse únicamente sobre la cuenta correspondiente:

```sql
UPDATE usuarios
SET tipo = 'ADMIN'
WHERE email = 'mario.prueba@ejemplo.com';
```

3. Verificar el rol en la base de datos.
4. Autenticarse en Postman con el correo y la contraseña original elegidos durante el registro.


## 5. Endpoints/Acceso de la API

URL base local: `http://localhost:8080`

URL base publicada: `https://agenda-salas.onrender.com`

### 5.1. Autenticación y registro

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/auth/registro` | Público |

### 5.2. Usuarios

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/usuarios` | ADMIN |
| GET | `/api/usuarios/{id}` | ADMIN |
| POST | `/api/usuarios` | ADMIN |
| PUT | `/api/usuarios/{id}` | ADMIN |
| DELETE | `/api/usuarios/{id}` | ADMIN |

El endpoint de registro público es `/api/auth/registro`. El CRUD de `/api/usuarios` está protegido y reservado al administrador.

### 5.3. Salas

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/salas` | Cualquier usuario autenticado |
| GET | `/api/salas/{id}` | Cualquier usuario autenticado |
| POST | `/api/salas` | ADMIN |
| PUT | `/api/salas/{id}` | ADMIN |
| DELETE | `/api/salas/{id}` | ADMIN |

### 5.4. Reservas

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/reservas` | Cualquier usuario autenticado |
| GET | `/api/reservas/{id}` | Cualquier usuario autenticado |
| GET | `/api/reservas/usuario/{usuarioId}` | Cualquier usuario autenticado |
| GET | `/api/reservas/sala/{salaId}` | Cualquier usuario autenticado |
| POST | `/api/reservas` | Cualquier usuario autenticado |
| PUT | `/api/reservas/{id}` | Cualquier usuario autenticado |
| DELETE | `/api/reservas/{id}` | Cualquier usuario autenticado |


## 6. Ejemplos de uso con Postman

Para las solicitudes con cuerpo JSON, seleccionar **Body → raw → JSON** y utilizar el encabezado `Content-Type: application/json`.

### 6.1. Registrar un usuario sin autenticación

**Solicitud:**

`POST /api/auth/registro`

```json
{
  "nombre": "Ana",
  "apellido": "Gomez",
  "email": "ana.prueba@ejemplo.com",
  "password": "ClaveDemo123"
}
```

**Resultado esperado:** `201 Created`, con los datos del usuario y sin contraseña ni hash.

### 6.2. Probar un correo duplicado

Enviar nuevamente el registro con el mismo correo.

**Resultado esperado:** `409 Conflict`, sin crear otro usuario.

### 6.3. Probar datos inválidos

Enviar un registro con una contraseña inferior a 6 caracteres:

```json
{
  "nombre": "Ana",
  "apellido": "Gomez",
  "email": "ana.invalida@ejemplo.com",
  "password": "123"
}
```

**Resultado esperado:** `400 Bad Request`, con un mensaje que indique el incumplimiento de la validación.

### 6.4. Consultar salas con autenticación

**Solicitud:**

`GET /api/salas`

En Authorization, seleccionar Basic Auth e ingresar el correo y la contraseña de un usuario registrado.

**Resultado esperado:** `200 OK`.

### 6.5. Intentar crear una sala como CLIENTE

**Solicitud:**

`POST /api/salas`

```json
{
  "numero": 99,
  "capacidad": 10,
  "estado": "DISPONIBLE"
}
```

**Resultado esperado:** `403 Forbidden`. La sala no debe crearse y en la respuesta del error debe existir el mensaje: "No tiene permisos para realizar esta operación" 

### 6.6. Crear una sala como ADMIN

Enviar la misma solicitud autenticándose con una cuenta que tenga rol `ADMIN`.

**Resultado esperado:** `201 Created`, con los datos de la sala creada.

### 6.7. Crear una reserva como CLIENTE

**Solicitud:**

`POST /api/reservas`

```json
{
  "salaId": "4",
  "fecha": "2026-12-12",
  "estado": "CONFIRMADA",
  "horaInicio": "10:00:00",
  "horaFin": "12:00:00",
  "usuarioId": 5
}
```

El usuario y la sala indicados deben existir y el horario debe estar disponible, con horarios válidos segun las reglas.

**Resultado esperado:** `201 Created`, con los datos de la reserva.

### 6.8. Consultar reservas por relación

Todas las reservas:

`GET /api/reservas`

Reservas de un usuario:

`GET /api/reservas/usuario/2`

Reservas de una sala:

`GET /api/reservas/sala/2`

Estas consultas requieren autenticación y permiten verificar las relaciones entre `Reserva`, `Usuario` y `Sala`.

### 6.9. Comprobar errores de autenticación y autorización

| Escenario | Resultado esperado |
|---|---|
| Consultar salas sin credenciales | `401 Unauthorized` |
| Consultar salas con contraseña incorrecta | `401 Unauthorized` |
| Crear una sala como CLIENTE | `403 Forbidden` |
| Modificar una sala como CLIENTE | `403 Forbidden` |
| Eliminar una sala como CLIENTE | `403 Forbidden` |
| Consultar usuarios como CLIENTE | `403 Forbidden` |
| Consultar usuarios como ADMIN | `200 OK` |

La respuesta de error incluye un código HTTP, un mensaje comprensible y una fecha. Las operaciones rechazadas por la autorización no deben ejecutar la modificación solicitada.

## 7. Validaciones y reglas de negocio

### Usuarios
- Nombre, apellido, correo, contraseña y rol son obligatorios según la operación.
- Nombre y apellido tienen una longitud máxima de 80 caracteres.
- El correo debe tener formato válido, una longitud máxima de 150 caracteres y ser único.
- En el registro público, la contraseña debe tener entre 6 y 50 caracteres.
- Las contraseñas se almacenan con BCrypt.
- El registro público asigna `CLIENTE` automáticamente.

### Salas
- El número y la capacidad deben ser válidos y positivos.
- El estado debe corresponder a un valor permitido.
- No se permiten números de sala duplicados.

### Reservas
- La fecha, el horario, el usuario, la sala y el estado deben ser válidos.
- La fecha no puede ser anterior al día actual.
- La hora de inicio debe ser anterior a la hora de finalización.
- El usuario y la sala asociados deben existir.
- No se permiten superposiciones entre reservas confirmadas de una misma sala en una misma fecha.
- Dos reservas consecutivas, donde una termina exactamente cuando comienza la otra, no se consideran superpuestas.

## 8. Manejo de errores y códigos HTTP

| Código | Significado | Ejemplo |
|---|---|---|
| `200 OK` | Operación exitosa | Consulta o actualización |
| `201 Created` | Recurso creado | Registro de usuario, sala o reserva |
| `204 No Content` | Operación exitosa sin respuesta | Eliminación |
| `400 Bad Request` | Solicitud inválida | Campos faltantes o formato incorrecto |
| `401 Unauthorized` | Falta autenticación o las credenciales son inválidas | Basic Auth ausente o incorrecto |
| `403 Forbidden` | Usuario autenticado sin permisos suficientes | CLIENTE intenta crear una sala |
| `404 Not Found` | Recurso inexistente | Identificador no encontrado |
| `409 Conflict` | Conflicto con los datos existentes | Correo duplicado o conflicto de integridad |

Los errores de validación, negocio, autenticación y autorización se responden mediante mensajes JSON. La estructura concreta depende del manejador que corresponda a la excepción.

## 9. Configuración de la base de datos

La aplicación utiliza MySQL y las siguientes variables de entorno:

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de conexión |
| `DB_USERNAME` | Usuario de la base |
| `DB_PASSWORD` | Contraseña de la base |

Para una instalación local, crear una base de datos:

```sql
CREATE DATABASE agenda_salas;
```

En nuestro caso, utilizamos una base de datos alojada en AIVEN.

Ejemplo de configuración local:

```text
DB_URL=jdbc:mysql://localhost:3306/agenda_salas
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
```

Configurar las variables en el entorno de ejecución de IntelliJ IDEA.

En Render y Aiven, las variables correspondientes deben configurarse en el panel del proveedor. La configuración del esquema utiliza Hibernate con `spring.jpa.hibernate.ddl-auto=update`, en este proyecto no utilizamos migraciones.

## 10. Instalación y ejecución local

Antes de realizar la instalación local recomendamos utilizar el despliegue en Render.

### Requisitos para instalación local
- JDK 21.
- Git.
- MySQL local o acceso a una instancia compatible.
- Postman para ejecutar las solicitudes.

### Paso 1. Clonar el repositorio

```bash
git clone https://github.com/FabiPaez/agenda-salas.git
cd agenda-salas
```

### Paso 2. Configurar la base de datos

Definir `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en la configuración de ejecución y verificar que la base de datos esté accesible.

### Paso 3. Compilar el proyecto

En Windows:

```powershell
.\mvnw.cmd clean package
```

En Linux o macOS:

```bash
./mvnw clean package
```

### Paso 4. Ejecutar la aplicación

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

También se puede ejecutar `AgendaSalasApplication` desde IntelliJ IDEA.

Por defecto, la aplicación utiliza el puerto `8080`.

### Paso 5. Probar la API

- Inicio: `http://localhost:8080/`
- Registro público: `http://localhost:8080/api/auth/registro`
- Usuarios: `http://localhost:8080/api/usuarios`
- Salas: `http://localhost:8080/api/salas`
- Reservas: `http://localhost:8080/api/reservas`

Las rutas protegidas requieren Basic Auth en Postman.

## 11. Organización del código

El proyecto separa las responsabilidades en las siguientes capas:

- **Controller:** expone los endpoints y recibe las solicitudes HTTP.
- **DTO:** define los datos de entrada y salida.
- **Service:** implementa las reglas de negocio.
- **Repository:** realiza el acceso a datos mediante Spring Data JPA.
- **Entity:** representa las entidades persistidas.
- **Mapper:** transforma entidades en DTO.
- **Config:** contiene la configuración de seguridad y el codificador de contraseñas.
- **Exception Handler:** centraliza el tratamiento de errores.

## 12. Alcance y mejoras futuras

La entrega mantiene el CRUD y las relaciones JPA implementadas en la primera versión e incorpora registro seguro, autenticación y autorización por roles.

Como posibles mejoras futuras se consideran:
- Restringir la gestión de reservas según la propiedad de cada reserva.
- Incorporar pruebas automatizadas más amplias.
- Incorporar migraciones versionadas de la base de datos.
- Ampliar las funcionalidades relacionadas con películas y funciones.

## 13. Información de la entrega académica

- **Repositorio:** https://github.com/FabiPaez/agenda-salas
- **Rama:** `main`
- **Commit entregado:** "Preparando Segundo Entregable".
- **Integrantes:** Fabián Páez, Facundo Santana y Pablo Cortez.