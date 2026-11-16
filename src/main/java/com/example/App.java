package com.example;

import java.io.File;
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
import javafx.event.Event;
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
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;

public class App extends Application {

    // Atributos de la clase (accesibles desde todos los métodos)
    private Stage ventana;
    private Scene escenaPrincipal;
    private VBox panelBotonesNotas;
    private TextArea txtVisualizacionNota;
    private Nota notaSeleccionada = null;
    private final String CARPETA_NOTAS = "notasGuardadas";

    @Override
    public void start(Stage ventana) {
        this.ventana = ventana;
        this.ventana.setTitle("Proyecto 1");

        // Aseguramos que la carpeta exista desde el principio
        new File(CARPETA_NOTAS).mkdirs();

        // 1. Construimos la interfaz principal
        VBox pantallaPrincipal = new VBox();
        pantallaPrincipal.getChildren().addAll(crearPanelSuperior(), crearPanelCentral());

        escenaPrincipal = new Scene(pantallaPrincipal, 800, 600);
        ventana.setScene(escenaPrincipal);

        // 2. Cargamos los botones de las notas guardadas
        actualizarListaNotas();

        ventana.show();
    }

    // --- MÉTODOS DE CONSTRUCCIÓN DE INTERFAZ ---

    private BorderPane crearPanelSuperior() {
        Button btnNueva = new Button("Nueva Nota");
        Button btnEditar = new Button("Editar Nota");
        Button btnBorrar = new Button("Borrar Nota");
        TextField txtBuscar = new TextField();
        Button btnBuscar = new Button("Buscar Nota");

        // Asignamos acciones limpias con lambdas
        btnNueva.setOnAction(e -> abrirFormularioNota(null)); // null = nota nueva
        btnEditar.setOnAction(e -> {
            if (notaSeleccionada != null) {
                abrirFormularioNota(notaSeleccionada); // pasamos la nota a editar
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox panelBotones = new HBox(10, btnNueva, btnEditar, btnBorrar, spacer, txtBuscar, btnBuscar);
        BorderPane panelSuperior = new BorderPane();
        panelSuperior.setPadding(new Insets(20));
        panelSuperior.setTop(panelBotones);
        return panelSuperior;
    }

    private HBox crearPanelCentral() {
        // Panel izquierdo (Lista de notas)
        Label lblNotas = new Label("Notas");
        panelBotonesNotas = new VBox(10);
        panelBotonesNotas.setPadding(new Insets(10));

        ScrollPane scrollNotas = new ScrollPane(panelBotonesNotas);
        scrollNotas.setFitToWidth(true);
        scrollNotas.setMinWidth(150);
        VBox.setVgrow(scrollNotas, Priority.ALWAYS);

        VBox panelEleccionNotas = new VBox(10, lblNotas, scrollNotas);
        panelEleccionNotas.setPadding(new Insets(10));

        // Panel derecho (Visualización)
        txtVisualizacionNota = new TextArea();
        txtVisualizacionNota.setEditable(false);
        txtVisualizacionNota.setWrapText(true);
        VBox.setVgrow(txtVisualizacionNota, Priority.ALWAYS);

        VBox panelVisualizacionNotas = new VBox(txtVisualizacionNota);
        panelVisualizacionNotas.setStyle("-fx-background-color: #58A1CC; -fx-padding:10;");
        HBox.setHgrow(panelVisualizacionNotas, Priority.ALWAYS);

        HBox panelNotas = new HBox(panelEleccionNotas, panelVisualizacionNotas);
        VBox.setVgrow(panelNotas, Priority.ALWAYS);
        return panelNotas;
    }

    // --- FORMULARIO UNIFICADO (SIRVE PARA CREAR Y EDITAR) ---

    private void abrirFormularioNota(Nota notaAEditar) {
        boolean esEdicion = (notaAEditar != null);

        Label lblTitulo = new Label("Título:");
        TextField txtTitulo = new TextField(esEdicion ? notaAEditar.getTitulo() : "");

        Label lblCategoria = new Label("Categoría:");
        TextField txtCategoria = new TextField(esEdicion ? notaAEditar.getCategoria() : "");

        Label lblContenido = new Label("Contenido:");
        TextArea txtContenido = new TextArea(esEdicion ? notaAEditar.getContenido() : "");
        VBox.setVgrow(txtContenido, Priority.ALWAYS);

        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox botones = new HBox(10, btnGuardar, spacer, btnCancelar);

        VBox panelFormulario = new VBox(10, lblTitulo, txtTitulo, lblCategoria, txtCategoria, lblContenido,
                txtContenido, botones);
        panelFormulario.setPadding(new Insets(15));

        // Acción Guardar
        btnGuardar.setOnAction(e -> {
            Nota notaResultante = new Nota(txtTitulo.getText(), txtCategoria.getText(), txtContenido.getText());
            try {
                if (esEdicion) {
                    modificarFichero(notaResultante, notaAEditar.getTitulo(), CARPETA_NOTAS);
                    notaSeleccionada = notaResultante;
                    txtVisualizacionNota.setText(notaResultante.toString());
                } else {
                    crearFichero(notaResultante, CARPETA_NOTAS);
                }
                actualizarListaNotas(); // Refrescamos los botones de la izquierda
            } catch (IOException ex) {
                System.out.println("Error de fichero: " + ex.getMessage());
                ex.printStackTrace();
            }
            ventana.setScene(escenaPrincipal);
        });

        // Acción Cancelar
        btnCancelar.setOnAction(e -> ventana.setScene(escenaPrincipal));

        ventana.setScene(new Scene(panelFormulario, 400, 400));
    }

    // --- MÉTODO PARA RECARGAR LA LISTA LATERAL ---

    private void actualizarListaNotas() {
        panelBotonesNotas.getChildren().clear(); // Limpiamos botones viejos
         ArrayList<Nota> listaNotas = new ArrayList<>();
        try {
            listaNotas = cargarNotasDesdeDirectorio(CARPETA_NOTAS);
        } catch (IOException e) {
            System.out.println("Error al cargar notas: " + e.getMessage());
            return;
        }

        for (Nota n : listaNotas) {
            Button btnNota = new Button(n.getTitulo());
            btnNota.setMaxWidth(Double.MAX_VALUE); // Para que todos los botones tengan el mismo ancho

            btnNota.setOnAction(e -> {
                notaSeleccionada = n; // Ya tenemos el objeto Nota en memoria, no hace falta volver a leer el disco
                txtVisualizacionNota.setText(n.toString());
            });

            panelBotonesNotas.getChildren().add(btnNota);
        }
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
