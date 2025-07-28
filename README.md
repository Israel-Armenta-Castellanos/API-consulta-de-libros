LiteraturaAPI
Descripción
Proyecto en Spring Boot que consume la API pública Gutendex para buscar libros por título y maneja una base de datos PostgreSQL con información de autores.

El proyecto incluye:

Consumo de API REST externa para obtener libros.

Conversión de JSON a objetos Java con Jackson.

Persistencia de autores en base de datos con JPA/Hibernate.

Búsquedas de autores por nombre y filtrados en la base.

Manejo de errores comunes de persistencia.

Menú interactivo de consola para búsquedas.

Estructura principal
com.alura.literatura.model: Clases modelo para Autor, Libro, y registros de datos JSON (como DatosLibros, DatosAutor).

com.alura.literatura.repository: Repositorios JPA para Autor y Libro, con métodos personalizados.

com.alura.literatura.service: Servicios para consumo de API (ConsumoAPI) y conversión de datos JSON (ConvierteDatos).

com.alura.literatura.principal.Principal: Clase con menú para interacción por consola.

LiteraturaApplication: Clase principal que arranca la aplicación.

onfiguración base de datos
Configura tu conexión a PostgreSQL en application.properties:

properties
Copiar
Editar
spring.datasource.url=jdbc:postgresql://localhost:5432/tu_basedatos
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

Problemas comunes
Error de restricción de columna NOT NULL: verificar columnas en base y entidad.

Fallos en conexión a base de datos: revisar credenciales y URL.

Manejo de JSON con campos que pueden ser nulos o variar.

Autor
Israel Armenta Castellanos
