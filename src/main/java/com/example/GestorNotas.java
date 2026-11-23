package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GestorNotas {
    private final String directorio;

    public GestorNotas(String directorio) { // Constructor que recibe el directorio donde se almacenarán las notas
        this.directorio = directorio;
    }

    public String formatearFecha(LocalDateTime fecha) { // Formatea la fecha en el formato MMddyyHHmmss y devuelve el nombre del fichero con extensión .txt
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyHHmmss");
        return fecha.format(formatter) + ".txt";
    }

    public void crearFichero(Nota nota) throws IOException { // Crea un fichero con el contenido de la nota en el directorio especificado
        Path carpetaDestino = Path.of(directorio);
        if (!Files.exists(carpetaDestino)) {
            Files.createDirectories(carpetaDestino);
        }

        LocalDateTime fecha = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // Trunca la fecha a segundos para evitar problemas de precisión
        nota.setFecha(fecha);
        String nombreFichero = formatearFecha(fecha);
        nota.setNombreFichero(nombreFichero);

        Path ruta = Path.of(directorio, nombreFichero);
        Files.createFile(ruta);
        Files.writeString(ruta, nota.toString());
    }

    public void modificarFichero(Nota nota, String nombreFichero) throws IOException { // Modifica el contenido del fichero existente con el contenido de la nota
        if (nombreFichero == null || nombreFichero.isBlank()) {
            throw new IOException("La nota no tiene un nombre de fichero asignado.");
        }
        Path ruta = Path.of(directorio, nombreFichero);
        if (!Files.exists(ruta)) {
            throw new IOException("El fichero no existe: " + ruta.toAbsolutePath());
        }
        Files.writeString(ruta, nota.toString());
    }

    public void borrarFichero(String nombreFichero) throws IOException { // Borra el fichero con el nombre especificado
        Path ruta = Path.of(directorio, nombreFichero);
        if (!Files.deleteIfExists(ruta)) {
            throw new IOException("El fichero no existe y no se pudo borrar.");
        }
    }

    public Nota leerFichero(String nombreFichero) throws IOException { // Lee el contenido del fichero y crea un objeto Nota con los datos leídos
        Path ruta = Path.of(directorio, nombreFichero);
        List<String> lineas = Files.readAllLines(ruta);

        if (lineas.size() >= 3) {
            String titulo = lineas.get(0).replace("título: ", "");
            String categoria = lineas.get(1).replace("Categoría: ", "");
            StringBuilder contenidoBuilder = new StringBuilder();
            for (int i = 3; i < lineas.size(); i++) {
                contenidoBuilder.append(lineas.get(i)).append("\n");
            }
            Nota nota = new Nota(titulo, categoria, contenidoBuilder.toString().trim());
            nota.setNombreFichero(nombreFichero);
            return nota;
        } else {
            throw new IOException("El fichero no tiene el formato esperado.");
        }
    }

    public ArrayList<Nota> cargarNotas() throws IOException { // Carga todas las notas desde el directorio especificado y devuelve una lista de objetos Nota
        ArrayList<Nota> notas = new ArrayList<>();
        Path dirPath = Path.of(directorio);
        if (!Files.exists(dirPath)) {
            return notas;
        }

        // Filtra los ficheros que terminan con .txt y los lee para crear objetos Nota
        Files.list(dirPath)
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".txt")) 
                .forEach(path -> {
                    try {
                        Nota nota = leerFichero(path.getFileName().toString());
                        if (nota != null) notas.add(nota);
                    } catch (IOException e) {
                        System.out.println("Error al leer el fichero: " + e.getMessage());
                    }
                });
        return notas;
    }

    public List<Nota> filtrarNotas(List<Nota> notas, String textoBusqueda) { // Filtra las notas que contienen el texto de búsqueda en el título o contenido, ignorando mayúsculas y minúsculas
        if (textoBusqueda == null || textoBusqueda.isBlank()) {
            return notas; // Si está vacío, devuelve todas las notas
        }
        String busquedaMinuscula = textoBusqueda.toLowerCase();
        List<Nota> filtradas = new ArrayList<>();
        for (Nota nota : notas) {
            if (nota.getTitulo().toLowerCase().contains(busquedaMinuscula) ||
                nota.getContenido().toLowerCase().contains(busquedaMinuscula)) {
                filtradas.add(nota);
            }
        }
        return filtradas;
    }
}

