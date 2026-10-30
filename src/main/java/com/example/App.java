package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        Nota modificadaNota = new Nota("Mi primera nota modificada", "Trabajo",
                "Este es el contenido modificado de mi primera nota.");
        String nombre = formaterFecha(nota.getFecha());
        try {
            modificarFichero(modificadaNota, nombre);
        } catch (IOException e) {
            System.out.println("Error al modificar el fichero: " + e.getMessage());
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
        LocalDateTime fecha = LocalDateTime.now();
        nota.setFecha(fecha);
        String nombreFichero = formaterFecha(fecha);
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

}