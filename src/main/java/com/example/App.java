package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;

public class App extends Application {

    @Override
    public void start(Stage ventana) throws Exception {
        ventana.setTitle("Proyecto 1");

        VBox pantallaPrincipal = new VBox();

        VBox.setVgrow(pantallaPrincipal, Priority.ALWAYS);

        BorderPane panelSuperior = new BorderPane();
        HBox panelBotones = new HBox();

        panelBotones.setSpacing(10);

        // Panel superior
        Button btnNueva = new Button("Nueva Nota");
        Button btnEditar = new Button("Editar Nota");
        Button btnBorrar = new Button("Borrar Nota");
        TextField txtBuscar = new TextField();
        Button btnBuscar = new Button("Buscar Nota");
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        panelBotones.getChildren().addAll(btnNueva, btnEditar, btnBorrar, spacer, txtBuscar, btnBuscar);
        panelSuperior.setPadding(new javafx.geometry.Insets(20));
        panelSuperior.setTop(panelBotones);

        // Panel notas
        HBox panelNotas = new HBox();
        VBox.setVgrow(panelNotas, Priority.ALWAYS);

        // Panel elección de notas
        VBox panelEleccionNotas = new VBox(10);

        panelEleccionNotas.setPadding(new javafx.geometry.Insets(10));
        Label lblNotas = new Label("Notas");
        VBox panelListaNotas = new VBox();
        panelEleccionNotas.getChildren().addAll(lblNotas);

        // Panel visualización de notas
        VBox panelVisualizacionNotas = new VBox();
        panelVisualizacionNotas.setStyle("-fx-background-color: #58A1CC; -fx-padding:10;");

        TextArea txtVisualizacionNota = new TextArea();
        txtVisualizacionNota.setEditable(false);
        txtVisualizacionNota.setWrapText(true);

        HBox.setHgrow(panelVisualizacionNotas, Priority.ALWAYS);
        VBox.setVgrow(txtVisualizacionNota, Priority.ALWAYS);

        VBox panelBotonesNotas = new VBox(10);
        panelBotonesNotas.setPadding(new javafx.geometry.Insets(10));
        ScrollPane scrollNotas = new ScrollPane(panelBotonesNotas);
        scrollNotas.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollNotas.setMinWidth(150);
        VBox.setVgrow(scrollNotas, Priority.ALWAYS);

        scrollNotas.setFitToWidth(true);
        ArrayList<Nota> lisTaNotas = cargarNotasDesdeDirectorio("notasGuardadas");
        ArrayList<Button> botonesNotas = new ArrayList<>();
        for (Nota l : lisTaNotas) {
            Button btnNota = new Button(l.getTitulo());
            botonesNotas.add(btnNota);
            panelBotonesNotas.getChildren().add(btnNota);
            btnNota.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    txtVisualizacionNota.setText(l.toString());
                }
            });
        }

        panelVisualizacionNotas.getChildren().add(txtVisualizacionNota);
        panelEleccionNotas.getChildren().addAll(panelListaNotas, scrollNotas);

        // Construcción del panel de notas
        panelNotas.getChildren().addAll(panelEleccionNotas, panelVisualizacionNotas);

        pantallaPrincipal.getChildren().addAll(panelSuperior, panelNotas);

        Scene escena = new Scene(pantallaPrincipal, 800, 600);

        ventana.setScene(escena);

        ventana.show();
    }

    public static void main(String[] args) {
        launch();
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
            String titulo = lineas.get(0).replace("título: ", "");
            String categoria = lineas.get(1).replace("Categoría: ", "");
            StringBuilder contenidoBuilder = new StringBuilder();
            for (int i = 3; i < lineas.size(); i++) {
                contenidoBuilder.append(lineas.get(i)).append("\n");
            }
            String contenido = contenidoBuilder.toString().trim();

            Nota nota = new Nota(titulo, categoria, contenido);
            return nota;
        } else {
            throw new IOException("El fichero no tiene el formato esperado.");
        }
    }

    public static ArrayList<Nota> cargarNotasDesdeDirectorio(String directorio) throws IOException {
        // Implementación para cargar todas las notas desde un directorio
        ArrayList<Nota> notas = new ArrayList<>();
        Files.list(Path.of(directorio))
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".txt"))
                .forEach(path -> {
                    try {
                        Nota nota = leerFichero(path.getFileName().toString(), directorio);
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
        if (textoBusqueda == null || textoBusqueda.strip().isEmpty()) {
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
