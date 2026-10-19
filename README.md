# Agenda de Salas

API REST para la gestión de reservas de salas de cine, evitando superposición de las mismas.

## 1. Información del proyecto

**Equipo de desarrollo**
- Fabián Páez
- Facundo Santana
- Pablo Cortez

**Tecnologías utilizadas**
- Java 21
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Bean Validation
- Docker

**Repositorio:** https://github.com/FabiPaez/agenda-salas

**Último Commit correspondiente al primer entregable:** "Preparando README para Primer Entregable" 

**Rama de entrega:** `main`

Utilizamos Render y AIVEN para desplegar el backend y la base de datos, respectivamente:

**Aplicación desplegada (backend):** https://agenda-salas.onrender.com  (La versión gratuita que utilizamos suele demorarse en la primera conexión)

**Base de Datos desplegada en AIVEN** 

La conexión entre Render y AIVEN se realiza mediante configuración de variables de entorno.

## 2. Descripción y objetivo

Agenda de Salas es una API REST desarrollada para administrar las reservas de salas de un cine, realizadas por usuarios.

El proyecto permite registrar usuarios, gestionar las salas disponibles y crear, consultar, modificar o cancelar eliminando reservas mediante solicitudes HTTP.

La aplicación está orientada al personal encargado de administrar las salas y sus reservas. No constituye, en esta primera versión, un sistema completo de venta de entradas ni de gestión de películas y funciones.

## 3. Modelo de datos

El sistema cuenta con tres entidades principales: `Usuario`, `Sala` y `Reserva`.

### Usuario

Representa a una persona registrada en el sistema.

Se dejan planteados 3 tipos de usuarios, proyectando una eventual escalabilidad.

Atributos:
- `id`: identificador único.
- `nombre`: nombre del usuario.
- `apellido`: apellido del usuario.
- `email`: correo electrónico único.
- `contrasena`: contraseña recibida al registrar el usuario.
- `tipo`: tipo de usuario (`CLIENTE`, `PERSONAL` o `ADMIN`).

### Sala

Representa una sala que puede ser reservada.

Atributos:
- `id`: identificador único.
- `numero`: número identificador de la sala, único.
- `capacidad`: capacidad de la sala.
- `estado`: estado de la sala (`DISPONIBLE`, `MANTENIMIENTO` o `INACTIVA`).

### Reserva

Representa una reserva de una sala para una fecha y un intervalo horario determinados.

Atributos:
- `id`: identificador único.
- `fecha`: fecha de la reserva.
- `horaInicio`: hora de inicio.
- `horaFin`: hora de finalización.
- `usuario`: usuario asociado a la reserva.
- `sala`: sala reservada.
- `estado`: estado de la reserva (`CONFIRMADA` o `CANCELADA`).

### Relaciones y Cardinalidades

- Un **Usuario** puede registrar de cero a muchas (`1:N`) **Reservas**.
- Una **Sala** puede tener asignadas de cero a muchas (`1:N`) **Reservas**.
- Cada **Reserva** pertenece estrictamente a un (`N:1`) **Usuario** y a una (`N:1`) **Sala**.

Las relaciones se implementan mediante JPA utilizando asociaciones `@ManyToOne` desde `Reserva` hacia `Usuario` y `Sala`. Las claves foráneas permiten mantener la integridad referencial de la información.

## 4. Funcionalidades implementadas

La API permite realizar operaciones CRUD (crear, consultar, actualizar y eliminar) sobre usuarios, salas y reservas.

También incluye consultas de reservas por usuario y por sala, validaciones de datos y control de conflictos horarios.

### URL base

En el entorno local:

`http://localhost:8080`

En el entorno desplegado:

`https://agenda-salas.onrender.com`

### Endpoints de usuarios

| Método HTTP | Ruta | Descripción |
|---|---|---|
| GET | `/api/usuarios` | Lista los usuarios. |
| GET | `/api/usuarios/{id}` | Consulta un usuario por ID. |
| POST | `/api/usuarios` | Registra un usuario. |
| PUT | `/api/usuarios/{id}` | Actualiza un usuario. |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario. |

### Endpoints de salas

| Método HTTP | Ruta | Descripción |
|---|---|---|
| GET | `/api/salas` | Lista las salas. |
| GET | `/api/salas/{id}` | Consulta una sala por ID. |
| POST | `/api/salas` | Registra una sala. |
| PUT | `/api/salas/{id}` | Actualiza una sala. |
| DELETE | `/api/salas/{id}` | Elimina una sala. |

### Endpoints de reservas

| Método HTTP | Ruta | Descripción |
|---|---|---|
| GET | `/api/reservas` | Lista las reservas. |
| GET | `/api/reservas/{id}` | Consulta una reserva por ID. |
| GET | `/api/reservas/usuario/{usuarioId}` | Lista las reservas de un usuario. |
| GET | `/api/reservas/sala/{salaId}` | Lista las reservas de una sala. |
| POST | `/api/reservas` | Crea una reserva. |
| PUT | `/api/reservas/{id}` | Actualiza una reserva. |
| DELETE | `/api/reservas/{id}` | Elimina una reserva. |

**Aclaraciones sobre los métodos HTTP:**
- `GET`: consulta información.
- `POST`: crea un recurso.
- `PUT`: actualiza un recurso.
- `DELETE`: elimina un recurso.
- `PATCH`: no está implementado en esta versión.

La ruta raíz `/` muestra una página de texto con los principales endpoints disponibles.
ESto para evitar que nuestra API muestre un inicio Vacío

## 5. Ejemplos de uso

Las siguientes solicitudes se pueden ejecutar con Postman o cualquier herramienta que permita enviar peticiones HTTP.

Para las solicitudes `POST` y `PUT`, utilizar el encabezado:

`Content-Type: application/json`

### 5.1. Registrar una sala

**Solicitud**

`POST /api/salas`

```json
{
  "numero": 5,
  "capacidad": 65,
  "estado": "DISPONIBLE"
}
```

**Respuesta esperada:** `201 Created`.

```json
{
  "id": 5,
  "numero": 5,
  "capacidad": 65,
  "estado": "DISPONIBLE"
}
```

El numero de sala es posible que no coincida con el Identificador.
Ya que esto no es un requisto funcional.

### 5.2. Registrar un usuario

**Solicitud**

`POST /api/usuarios`

```json
{
  "nombre": "Ana",
  "apellido": "Gomez",
  "email": "ana.prueba@ejemplo.com",
  "contrasena": "ClaveDemo123",
  "tipo": "PERSONAL"
}
```

**Respuesta esperada:** `201 Created`.

```json
{
  "id": 4,
  "nombre": "Ana",
  "apellido": "Gomez",
  "email": "ana.prueba@ejemplo.com",
  "tipo": "PERSONAL"
}
```

La respuesta no incluye el atributo `contrasena`. Los datos de este ejemplo son ficticios y deben utilizarse únicamente para pruebas.

### 5.3. Crear una reserva

Para crear una reserva deben existir previamente el usuario y la sala indicados.

**Solicitud**

`POST /api/reservas`

```json
{
  "fecha": "2026-11-24",
  "horaInicio": "14:00:00",
  "horaFin": "16:00:00",
  "usuarioId": 3,
  "salaId": 1,
  "estado": "CONFIRMADA"
}
```

**Respuesta esperada:** `201 Created`.

```json
{
  "id": 5,
  "fecha": "2026-10-22",
  "horaInicio": "14:00:00",
  "horaFin": "16:00:00",
  "usuarioId": 2,
  "salaId": 1,
  "estado": "CONFIRMADA"
}
```

Los identificadores devueltos dependerán de los que existan en la base de datos. La reserva solo se creará si supera las validaciones del sistema.

### 5.4. Consultar reservas relacionadas

Listar todas las reservas:

`GET /api/reservas`

Consultar una reserva específica:

`GET /api/reservas/1`

Consultar las reservas de un usuario:

`GET /api/reservas/usuario/1`

Consultar las reservas de una sala:

`GET /api/reservas/sala/1`

Si el usuario o la sala indicados no existen, la API devuelve `404 Not Found`. Por ejemplo:

```json
{
  "error": "No existe el usuario con ID 8"
}
```

### 5.5. Actualizar y eliminar

Para actualizar una sala:

`PUT /api/salas/2`

```json
{
  "numero": 2,
  "capacidad": 25,
  "estado": "DISPONIBLE"
}
```

Para eliminar una reserva:

`DELETE /api/reservas/2`

Si la eliminación se realiza correctamente, la respuesta es `204 No Content`.

## 6. Validaciones y reglas de negocio

La API implementa validaciones para mantener la consistencia de los datos.

### Usuarios
- Nombre, apellido, email, contraseña y tipo son obligatorios.
- Nombre y apellido tienen una longitud máxima de 80 caracteres.
- El email debe tener un formato válido y no superar los 80 caracteres.
- La contraseña debe tener entre 8 y 50 caracteres.
- No se permiten emails duplicados.

### Salas
- El número y la capacidad son obligatorios y deben ser positivos.
- El estado debe corresponder a uno de los valores permitidos.
- No se permiten números de sala duplicados.

### Reservas
- La fecha, el horario, el usuario, la sala y el estado son obligatorios.
- La fecha no puede ser anterior al día actual.
- La hora de inicio debe ser anterior a la hora de finalización.
- El usuario y la sala asociados deben existir.
- No se permiten superposiciones entre reservas confirmadas de una misma sala en una misma fecha.

La comprobación de superposición considera que existe conflicto cuando el intervalo de una reserva se cruza con el de otra reserva confirmada. Los intervalos consecutivos, en los que una reserva termina exactamente cuando comienza la siguiente, no se consideran superpuestos.

## 7. Manejo de errores y códigos HTTP

La aplicación centraliza el tratamiento de excepciones para responder con códigos HTTP adecuados.

| Código HTTP | Significado | Situación habitual                                                                     |
|---|---|----------------------------------------------------------------------------------------|
| `200 OK` | Operación exitosa. | Consultas y actualizaciones.                                                           |
| `201 Created` | Recurso creado. | Registro de usuario, sala o reserva.                                                   |
| `204 No Content` | Operación exitosa sin contenido. | Eliminación.                                                                           |
| `400 Bad Request` | Solicitud inválida. | Campos obligatorios faltantes o datos incorrectos.                                     |
| `404 Not Found` | Recurso inexistente. | ID no encontrado.                                                                      |
| `409 Conflict` | Conflicto con el estado de los datos. | Email o número duplicado, superposición horaria de reservas o conflicto de integridad. |

Las respuestas de error pueden incluir un objeto JSON con la propiedad `error`, que contiene una descripción del problema.

## 8. Requisitos para ejecutar el proyecto

Para ejecutar la aplicación localmente se necesita:

- Java Development Kit (JDK) 21.
- Git, si se obtiene el código desde GitHub.
- Maven.

Recomendamos utilizar el despliegue de Render, ya se encuentra vinculado a AIVEN mediante variables de entorno.
https://agenda-salas.onrender.com

## 9. Configuración de la base de datos

La aplicación utiliza MySQL y obtiene los datos de conexión mediante variables de entorno, evitando almacenar credenciales en el código fuente.

Las variables requeridas son:

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC de conexión a MySQL. |
| `DB_USERNAME` | Usuario de la base de datos. |
| `DB_PASSWORD` | Contraseña de la base de datos. |

Para una instalación local, crear una base de datos llamada `agenda_salas` en MySQL. Por ejemplo:

```sql
CREATE DATABASE agenda_salas;
```

Configurar las variables de entorno con valores correspondientes a la instalación local:

```text
DB_URL=jdbc:mysql://localhost:3306/agenda_salas
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
```

En `application.properties` se utiliza `spring.jpa.hibernate.ddl-auto=update`, por lo que Hibernate actualiza el esquema de las tablas a partir de las entidades al iniciar la aplicación. Esta configuración resulta práctica para el desarrollo académico; para un entorno productivo se recomienda gestionar los cambios del esquema mediante migraciones.

## 10. Instalación y ejecución local

Antes de instalar y ejecutar localmente, recomendamos utilizar el despliegue en Render: https://agenda-salas.onrender.com, el cual puede recibir las peticiones en estos endpoints:

- Usuarios: `https://agenda-salas.onrender.com/api/usuarios`
- Salas: `https://agenda-salas.onrender.com/api/salas`
- Reservas: `https://agenda-salas.onrender.com/api/reservas`

A continuación se indican los pasos para instalar y ejecutar localmente:

### Paso 1. Obtener el proyecto

Clonar el repositorio:

```bash
git clone https://github.com/FabiPaez/agenda-salas.git
cd agenda-salas
```

### Paso 2. Configurar las variables de entorno

Definir `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en la configuración de ejecución de IntelliJ IDEA, o en el entorno desde el que se inicie la aplicación.

Verificar que el servicio MySQL esté activo y que la base de datos sea accesible.

### Paso 3. Compilar y ejecutar las pruebas

En Windows:

```powershell
.\mvnw.cmd clean verify
```

En Linux o macOS:

```bash
./mvnw clean verify
```

### Paso 4. Iniciar la aplicación

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

También se puede ejecutar la clase principal `AgendaSalasApplication` desde IntelliJ IDEA.

Por defecto, el servidor inicia en el puerto 8080.

### Paso 5. Probar la aplicación

Abrir en el navegador:

- Página de inicio: `http://localhost:8080/`
- Usuarios: `http://localhost:8080/api/usuarios`
- Salas: `http://localhost:8080/api/salas`
- Reservas: `http://localhost:8080/api/reservas`

Para crear, modificar y eliminar recursos, utilizar Postman o una herramienta equivalente.

## 11. Pruebas sugeridas para la evaluación

Se recomienda comprobar los siguientes escenarios:

1. Consultar las listas de usuarios, salas y reservas.
2. Crear una sala y verificar que se almacene correctamente.
3. Crear un usuario y comprobar que la respuesta no exponga su contraseña.
4. Crear una reserva con un usuario y una sala existentes.
5. Consultar las reservas por usuario y por sala.
6. Solicitar un ID inexistente y verificar la respuesta `404 Not Found`.
7. Intentar registrar un email o número de sala duplicado y comprobar el conflicto.
8. Intentar crear una reserva con horarios superpuestos para la misma sala y fecha.
9. Enviar datos inválidos y verificar la respuesta `400 Bad Request`.
10. Actualizar y eliminar recursos mediante `PUT` y `DELETE`.

## 12. Estructura general de la aplicación

La aplicación está organizada en capas para separar responsabilidades:

- **Controller:** recibe las solicitudes HTTP y expone los endpoints.
- **DTO / Request:** define los datos de entrada y sus validaciones.
- **Service:** contiene las reglas de negocio y coordina las operaciones.
- **Repository:** permite acceder a la base de datos mediante Spring Data JPA.
- **Entity:** representa las entidades persistidas.
- **Mapper:** transforma entidades en DTO de respuesta.
- **Exception Handler:** centraliza el tratamiento de errores y la construcción de respuestas HTTP.

Esta organización facilita el mantenimiento, las pruebas y la evolución del sistema.

## 13. Alcance y mejoras futuras

La primera versión se concentra en la gestión de usuarios, salas y reservas.

Entre las posibles mejoras para futuras entregas se encuentran:

- Incorporar autenticación y autorización por roles.
- Mejorar la protección y el almacenamiento de contraseñas.
- Agregar gestión de películas y funciones.
- Incorporar venta o reserva de entradas.
- Ampliar las pruebas automatizadas y la documentación de la API.
- Incorporar migraciones versionadas de la base de datos.