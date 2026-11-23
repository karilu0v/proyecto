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

    public GestorNotas(String directorio) {
        this.directorio = directorio;
    }

    public String formatearFecha(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyHHmmss");
        return fecha.format(formatter) + ".txt";
    }

    public void crearFichero(Nota nota) throws IOException {
        Path carpetaDestino = Path.of(directorio);
        if (!Files.exists(carpetaDestino)) {
            Files.createDirectories(carpetaDestino);
        }

        LocalDateTime fecha = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        nota.setFecha(fecha);
        String nombreFichero = formatearFecha(fecha);
        nota.setNombreFichero(nombreFichero);

        Path ruta = Path.of(directorio, nombreFichero);
        Files.createFile(ruta);
        Files.writeString(ruta, nota.toString());
    }

    public void modificarFichero(Nota nota, String nombreFichero) throws IOException {
        if (nombreFichero == null || nombreFichero.isBlank()) {
            throw new IOException("La nota no tiene un nombre de fichero asignado.");
        }
        Path ruta = Path.of(directorio, nombreFichero);
        if (!Files.exists(ruta)) {
            throw new IOException("El fichero no existe: " + ruta.toAbsolutePath());
        }
        Files.writeString(ruta, nota.toString());
    }

    public void borrarFichero(String nombreFichero) throws IOException {
        Path ruta = Path.of(directorio, nombreFichero);
        if (!Files.deleteIfExists(ruta)) {
            throw new IOException("El fichero no existe y no se pudo borrar.");
        }
    }

    public Nota leerFichero(String nombreFichero) throws IOException {
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

    public ArrayList<Nota> cargarNotas() throws IOException {
        ArrayList<Nota> notas = new ArrayList<>();
        Path dirPath = Path.of(directorio);
        if (!Files.exists(dirPath)) {
            return notas;
        }

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

    public List<Nota> filtrarNotas(List<Nota> notas, String textoBusqueda) {
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

