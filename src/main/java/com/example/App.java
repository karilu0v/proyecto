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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage ventana) throws Exception {
        ventana.setTitle("Proyecto 1");

        VBox pantallaPrincipal = new VBox();

        BorderPane panelSuperior = new BorderPane();
        HBox panelBotones = new HBox();

        panelBotones.setSpacing(10);
        

        Button btnNueva = new Button("Nueva Nota");
        Button btnEditar = new Button("Editar Nota");
        Button btnBorrar = new Button("Borrar Nota");
        TextField txtBuscar = new TextField();
        Button btnBuscar = new Button("Buscar Nota");
        panelBotones.getChildren().addAll(btnNueva, btnEditar, btnBorrar, txtBuscar, btnBuscar);
        panelSuperior.setTop(panelBotones);

       

        pantallaPrincipal.getChildren().addAll(panelSuperior);

        Scene escena = new Scene(pantallaPrincipal, 600, 400);

        ventana.setScene(escena);

        ventana.show();
    }

    public static void main(String[] args) {
        launch();
       /*try{
            List<Nota> notas = cargarNotasDesdeDirectorio("notasGuardadas");
            System.out.println("Notas cargadas desde el directorio:");
            for (Nota nota : notas) {
                System.out.println(nota);
                System.out.println();
            }
        }
        catch (IOException e){
            System.out.println("Error al cargar las notas: " + e.getMessage());
        }*/
        

    }

    public static String formaterFecha(LocalDateTime fecha) {
        // Implementación para formatear la fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyHHmmss");
        String nombreFichero = fecha.format(formatter) + ".txt";
        return nombreFichero;
    }

    public static void crearFichero(Nota nota, String directorio) throws IOException {
        // Implementación para crear un fichero
        Path carpetaDestino = Path.of(directorio);
        if (!Files.exists(carpetaDestino)) {
            Files.createDirectories(carpetaDestino);
        }

        // creación del nombre del fichero con la fecha y hora actual
        LocalDateTime fecha = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        nota.setFecha(fecha);
        String nombreFichero = formaterFecha(nota.getFecha());
        Path ruta = Path.of(directorio, nombreFichero);

        // Creación del fichero junto con el contenido de la nota

        Files.createFile(ruta);
        System.out.println("Fichero creado con éxito.");
        Files.writeString(ruta, nota.toString());
        System.out.println("Contenido escrito en el fichero.");

    }

    public static void modificarFichero(Nota nota, String nombreFichero, String directorio) throws IOException {
        // Implementación para modificar un fichero
        Path ruta = Path.of(directorio, nombreFichero);

        // Modificación del fichero
        if (!Files.exists(ruta)) {
            throw new IOException("El fichero no existe.");
        }
        Files.writeString(ruta, nota.toString());
        System.out.println("Contenido escrito en el fichero.");

    }

    public static void borrarFichero(Nota nota, String nombreFichero, String directorio) throws IOException {
        // Implementación para borrar un fichero
        Path ruta = Path.of(directorio, nombreFichero);

        // Borrado del fichero

        // Intenta borrar el archivo y nos avisa si existía o no
        if (Files.deleteIfExists(ruta)) {
            System.out.println("El fichero fue borrado con éxito.");
        } else {
           throw new IOException("El fichero no existe y no se pudo borrar.");
        }

    }

    public static Nota leerFichero(String nombreFichero, String directorio) throws IOException {
        // Implementación para leer un fichero
        Path ruta = Path.of(directorio, nombreFichero);

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

    public static List<Nota> cargarNotasDesdeDirectorio(String directorio) throws IOException {
        // Implementación para cargar todas las notas desde un directorio
        List<Nota> notas = new java.util.ArrayList<>();
        Files.list(Path.of(directorio))
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".txt"))
                .forEach(path -> {
                    try {
                        Nota nota = leerFichero( path.getFileName().toString(),directorio);
                        if (nota != null) {
                            notas.add(nota);
                        }
                    } catch (IOException e) {
                        System.out.println("Error al leer el fichero: " + e.getMessage());
                    }
                });
        return notas;
    }

    public List<Nota> filtrarNotas(List<Nota> notas, String textoBusqueda) throws IllegalArgumentException {
        // Implementación para filtrar notas por título o contenido
         if (textoBusqueda == null || textoBusqueda.strip().isEmpty()){
            throw new IllegalArgumentException("El texto de búsqueda no puede estar vacío o ser nulo.");
         }
        List<Nota> notasFiltradas = new java.util.ArrayList<>();
        for (Nota nota : notas) {
            if (nota.getTitulo().contains(textoBusqueda) || nota.getContenido().contains(textoBusqueda)) {
                notasFiltradas.add(nota);
            }
        }
        return notasFiltradas;
    }
}
