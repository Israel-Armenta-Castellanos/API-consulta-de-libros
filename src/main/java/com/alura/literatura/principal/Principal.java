package com.alura.literatura.principal;

import com.alura.literatura.model.*;
import com.alura.literatura.repository.AutorRepository;
import com.alura.literatura.repository.LibroRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;

import java.util.*;

public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private final Scanner teclado = new Scanner(System.in);
    private final ConsumoAPI consumoApi = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(teclado.nextLine());
                procesarOpcion(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Error: Debes ingresar un número válido.");
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("""
                \n=== MENÚ PRINCIPAL ===
                1 - Buscar libro por título
                2 - Listar libros registrados
                3 - Listar autores registrados
                4 - Listar autores vivos en un determinado año
                5 - Listar libros por idioma
                0 - Salir
                """);
        System.out.print("Seleccione una opción: ");
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> buscarLibrosPorTitulo();
            case 2 -> listarLibrosRegistrados();
            case 3 -> listarAutoresRegistrados();
            case 4 -> listarAutoresVivosEnDeterminadoAno();
            case 5 -> listarLibrosPorIdioma();
            case 0 -> System.out.println("Cerrando la aplicación...");
            default -> System.out.println("Opción inválida. Intente nuevamente.");
        }
    }

    private Optional<DatosLibros> buscarLibroEnAPI() {
        System.out.println("Escribe el nombre del libro que deseas buscar: ");
        String nombreLibro = teclado.nextLine();
        String url = URL_BASE + "?search=" + nombreLibro.replace(" ", "+");
        String json = consumoApi.obtenerDatos(url);
        Datos datos = conversor.obtenerDatos(json, Datos.class);

        return datos.resultados().stream()
                .filter(l -> l.titulo().toLowerCase().contains(nombreLibro.toLowerCase()))
                .findFirst();
    }

    private void buscarLibrosPorTitulo() {
        Optional<DatosLibros> datosLibro = buscarLibroEnAPI();

        if (datosLibro.isEmpty()) {
            System.out.println("Libro no encontrado.");
            return;
        }

        DatosLibros libroData = datosLibro.get();

        if (libroData.autor().isEmpty()) {
            System.out.println("El libro no tiene autor registrado.");
            return;
        }

        List<Autor> autoresLibro = new ArrayList<>();

        for (DatosAutor datosAutor : libroData.autor()) {
            List<Autor> encontrados = autorRepository.findByNombreContainingIgnoreCase(datosAutor.nombre());
            Autor autor = encontrados.isEmpty()
                    ? autorRepository.save(new Autor(datosAutor))
                    : encontrados.get(0);
            autoresLibro.add(autor);
        }

        Libro libro = new Libro(libroData, autoresLibro);
        try {
            libroRepository.save(libro);
            System.out.println("\nLibro guardado exitosamente:");
            mostrarDetallesLibro(libro);
        } catch (Exception e) {
            System.out.println("El libro ya está registrado en la base de datos.");
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        System.out.println("\n=== LIBROS REGISTRADOS ===");
        libros.forEach(this::mostrarDetallesLibro);
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        System.out.println("\n=== AUTORES REGISTRADOS ===");
        autores.forEach(this::mostrarDetallesAutor);
    }

    private void listarAutoresVivosEnDeterminadoAno() {
        System.out.print("Ingrese un año: ");
        try {
            int anio = Integer.parseInt(teclado.nextLine());
            List<Autor> autores = autorRepository.findAutoresVivosEnAnio(anio);

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en ese año.");
                return;
            }

            System.out.println("\n=== AUTORES VIVOS EN " + anio + " ===");
            autores.forEach(this::mostrarDetallesAutor);
        } catch (NumberFormatException e) {
            System.out.println("Error: Debes ingresar un año válido.");
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el código del idioma (es, en, pt, fr, etc.): ");
        String codigo = teclado.nextLine().trim();

        try {
            Idioma idioma = Idioma.fromString(codigo);
            List<Libro> libros = libroRepository.findByIdiomasContaining(String.valueOf(idioma));

            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados en ese idioma.");
                return;
            }

            System.out.println("\n=== LIBROS EN " + idioma + " ===");
            libros.forEach(this::mostrarDetallesLibro);
        } catch (IllegalArgumentException e) {
            System.out.println("Idioma no reconocido. Intente con códigos como 'es', 'en', 'fr', etc.");
        }
    }

    private void mostrarDetallesLibro(Libro libro) {
        System.out.println("\nTítulo: " + libro.getTitulo() +
                "\nAutores: " + libro.getAutores().stream().map(Autor::getNombre).toList() +
                "\nIdiomas: " + libro.getIdiomas() +
                "\nDescargas: " + libro.getNumeroDeDescargas() +
                "\n-----------------------");
    }

    private void mostrarDetallesAutor(Autor autor) {
        System.out.println("\nNombre: " + autor.getNombre() +
                "\nAño de nacimiento: " + autor.getAnioNacimiento() +
                "\nAño de fallecimiento: " + autor.getAnioFallecimiento() +
                "\n-----------------------");
    }
}
