package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage ventana) throws Exception {
        ventana.setTitle("Plantilla de app para JavaFX");

        VBox columna = new VBox();

        Button boton = new Button("o.o");
        Label etiqueta = new Label("My way home is through you");
        Button boton2 = new Button("Hellooooooo");

        columna.getChildren().addAll(boton, etiqueta, boton2);

        Scene escena = new Scene(columna, 600, 400);

        ventana.setScene(escena);

        ventana.show();
    }

    public static void main(String[] args) {
        // launch();
        Nota nota = new Nota("Mi primera nota", "Personal", "Este es el contenido de mi primera nota.");
        try {
            crearFichero(nota);
        } catch (IOException e) {
            System.out.println("Error al crear el fichero: " + e.getMessage());
        }
        String nombre = formaterFecha(nota.getFecha());// A tener en cuenta --> hay que conseguir la fecha antes de
                                                       // modificar
        Nota modificadaNota = new Nota("Mi primera nota modificada", "Trabajo",
                "Este es el contenido modificado de mi primera nota.");
        modificadaNota.setFecha(nota.getFecha());
        try {
            modificarFichero(modificadaNota, nombre);
        } catch (IOException e) {
            System.out.println("Error al modificar el fichero: " + e.getMessage());
        }
        /*
         * try {
         * borrarFichero(modificadaNota, "103026104624.txt");
         * } catch (IOException e) {
         * System.out.println("Error al borrar el fichero: " + e.getMessage());
         * }
         */
        try {
            Nota notaLeida = leerFichero(nombre);
            System.out.println("Nota leída del fichero:");
            System.out.println(notaLeida.toString());
        } catch (IOException e) {
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }

    }

    public static String formaterFecha(LocalDateTime fecha) {
        // Implementación para formatear la fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyHHmmss");
        String nombreFichero = fecha.format(formatter) + ".txt";
        return nombreFichero;
    }

    public static void crearFichero(Nota nota) throws IOException {
        // Implementación para crear un fichero

        // creación del nombre del fichero con la fecha y hora actual
        LocalDateTime fecha = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        nota.setFecha(fecha);
        String nombreFichero = formaterFecha(nota.getFecha());
        Path ruta = Path.of(nombreFichero);

        // Creación del fichero junto con el contenido de la nota

        Files.createFile(ruta);
        System.out.println("Fichero creado con éxito.");
        Files.writeString(ruta, nota.toString());
        System.out.println("Contenido escrito en el fichero.");

    }

    public static void modificarFichero(Nota nota, String nombreFichero) throws IOException {
        // Implementación para modificar un fichero
        Path ruta = Path.of(nombreFichero);

        // Modificación del fichero
        if (!Files.exists(ruta)) {
            System.out.println("El fichero no existe.");
            return;
        }
        Files.writeString(ruta, nota.toString());
        System.out.println("Contenido escrito en el fichero.");

    }

    public static void borrarFichero(Nota nota, String nombreFichero) throws IOException {
        // Implementación para borrar un fichero
        Path ruta = Path.of(nombreFichero);

        // Borrado del fichero

        // Intenta borrar el archivo y nos avisa si existía o no
        if (Files.deleteIfExists(ruta)) {
            System.out.println("El fichero fue borrado con éxito.");
        } else {
            System.out.println("El fichero no se pudo borrar porque no existe.");
        }

    }

    public static Nota leerFichero(String nombreFichero) throws IOException {
        // Implementación para leer un fichero
        Path ruta = Path.of(nombreFichero);

        // Lectura del fichero
        List<String> lineas = Files.readAllLines(ruta); // Lee todas las líneas del fichero
        if (lineas.size() >= 3) {
            String fechaStr = lineas.get(0).replace("Hora de creación: ", "").trim();
            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime fecha = LocalDateTime.parse(fechaStr, formateador);
            System.out.println("Fecha leída del fichero: " + fecha);
            String titulo = lineas.get(1);
            String categoria = lineas.get(2).replace("Categoría: ", "");
            StringBuilder contenidoBuilder = new StringBuilder();
            for (int i = 3; i < lineas.size(); i++) {
                contenidoBuilder.append(lineas.get(i)).append("\n");
            }
            String contenido = contenidoBuilder.toString().trim();

            Nota nota = new Nota(titulo, categoria, contenido);
            nota.setFecha(fecha);
            return nota;
        } else {
            throw new IOException("El fichero no tiene el formato esperado.");
        }
    }
}
