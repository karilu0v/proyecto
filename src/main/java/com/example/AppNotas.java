package com.example;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppNotas extends Application {

    private GestorNotas gestor = new GestorNotas("notasGuardadas");
    private Stage ventana;
    private Scene escenaPrincipal;
    private VBox panelBotonesNotas;
    private TextArea txtVisualizacionNota;
    private Nota notaSeleccionada = null;
    private String rutaCss = getClass().getResource("estilos.css").toExternalForm();

    @Override
    public void start(Stage primaryStage) {
        this.ventana = primaryStage;
        ventana.setTitle("Gestor de Notas");

        VBox pantallaPrincipal = new VBox();

        // barra superior con botones
        Button btnNueva = new Button("Nueva Nota");
        Button btnEditar = new Button("Editar Nota");
        Button btnBorrar = new Button("Borrar Nota");
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar nota...");
        Button btnBuscar = new Button("Buscar");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox panelBotones = new HBox(10, btnNueva, btnEditar, btnBorrar, spacer, txtBuscar, btnBuscar);
        panelBotones.setPadding(new Insets(15));

        // Panel central
        VBox panelEleccionNotas = new VBox(10);
        panelEleccionNotas.setPadding(new Insets(10));
        panelEleccionNotas.setPrefWidth(200);
        panelEleccionNotas.setMinWidth(200);

        panelBotonesNotas = new VBox(5);
        ScrollPane scrollNotas = new ScrollPane(panelBotonesNotas);
        scrollNotas.setId("scrollNotas");
        scrollNotas.setFitToWidth(true);
        VBox.setVgrow(scrollNotas, Priority.ALWAYS);
        Label lblNotasTitulo = new Label("Notas");
        lblNotasTitulo.setId("tituloNotas");
        lblNotasTitulo.setMaxWidth(Double.MAX_VALUE);
        lblNotasTitulo.setAlignment(javafx.geometry.Pos.CENTER);

        panelEleccionNotas.getChildren().addAll(lblNotasTitulo, scrollNotas);

        VBox panelVisualizacionNotas = new VBox();
        HBox.setHgrow(panelVisualizacionNotas, Priority.ALWAYS);

        txtVisualizacionNota = new TextArea();
        txtVisualizacionNota.setEditable(false);
        txtVisualizacionNota.setWrapText(true);
        VBox.setVgrow(txtVisualizacionNota, Priority.ALWAYS);
        panelVisualizacionNotas.getChildren().add(txtVisualizacionNota);

        HBox panelNotas = new HBox(panelEleccionNotas, panelVisualizacionNotas);
        VBox.setVgrow(panelNotas, Priority.ALWAYS);

        pantallaPrincipal.getChildren().addAll(panelBotones, panelNotas);
        escenaPrincipal = new Scene(pantallaPrincipal, 700, 450);

        // eventos de botones
        btnNueva.setOnAction(e -> abrirFormularioNota(null));

        btnEditar.setOnAction(e -> {
            if (notaSeleccionada != null) {
                abrirFormularioNota(notaSeleccionada);
            } else {
                mostrarAlerta("Aviso", "Selecciona primero una nota para editar.");
            }
        });

        btnBorrar.setOnAction(e -> eliminarNotaSeleccionada());

        btnBuscar.setOnAction(e -> buscarNotas(txtBuscar.getText()));

        // Carga inicial
        actualizarListaNotas(null);

        ventana.setScene(escenaPrincipal);
        ventana.show();

        // Aplicar estilos CSS
        panelBotones.setId("panelSuperior");
        panelEleccionNotas.setId("panelIzquierdo");
        panelVisualizacionNotas.setId("panelDerecho");
        panelVisualizacionNotas.setStyle(""); 
        btnNueva.getStyleClass().add("boton-superior");
        btnEditar.getStyleClass().add("boton-superior");
        btnBorrar.getStyleClass().add("boton-superior");
        btnBuscar.getStyleClass().add("boton-superior");
        txtBuscar.setId("campoBuscar");
        txtVisualizacionNota.setId("visorNota");
        //Vincular el CSS a la escena principal
        escenaPrincipal.getStylesheets().add(getClass().getResource("estilos.css").toExternalForm());

    }

    private void abrirFormularioNota(Nota notaAEditar) { //Ventana para crear o editar una nota. Si notaAEditar es null, se crea una nueva nota; de lo contrario, se edita la nota existente.
        boolean esEdicion = (notaAEditar != null);

        Label lblTitulo = new Label("Título:");
        lblTitulo.getStyleClass().add("etiqueta-formulario");
        TextField txtTitulo = new TextField(esEdicion ? notaAEditar.getTitulo() : "");
        txtTitulo.getStyleClass().add("campo-formulario");

        Label lblCategoria = new Label("Categoría:");
        lblCategoria.getStyleClass().add("etiqueta-formulario");
        TextField txtCategoria = new TextField(esEdicion ? notaAEditar.getCategoria() : "");
        txtCategoria.getStyleClass().add("campo-formulario");

        Label lblContenido = new Label("Contenido:");
        lblContenido.getStyleClass().add("etiqueta-formulario");
        TextArea txtContenido = new TextArea(esEdicion ? notaAEditar.getContenido() : "");
        txtContenido.setWrapText(true);
        txtContenido.getStyleClass().add("campo-formulario");
        VBox.setVgrow(txtContenido, Priority.ALWAYS);

        Button btnGuardar = new Button("Guardar");
        btnGuardar.getStyleClass().add("boton-superior"); // Reutilizamos el botón de la barra superior

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("boton-superior");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox panelFormulario = new VBox(10,
                lblTitulo, txtTitulo,
                lblCategoria, txtCategoria,
                lblContenido, txtContenido,
                new HBox(10, btnGuardar, spacer, btnCancelar));
        panelFormulario.setId("panelFormulario");
        panelFormulario.setPadding(new Insets(20));

        btnGuardar.setOnAction(e -> { 
            Nota notaResultante = new Nota(txtTitulo.getText(), txtCategoria.getText(), txtContenido.getText());
            try {
                if (esEdicion) {
                    String nombreFichero = notaAEditar.getNombreFichero();
                    gestor.modificarFichero(notaResultante, nombreFichero); // Modifica el fichero existente
                    notaResultante.setFecha(notaAEditar.getFecha());
                    notaResultante.setNombreFichero(nombreFichero);
                    notaSeleccionada = notaResultante;
                    txtVisualizacionNota.setText(notaResultante.toString());
                } else {
                    gestor.crearFichero(notaResultante);
                }
                actualizarListaNotas(null);
                ventana.setScene(escenaPrincipal);
            } catch (IOException ex) {
                mostrarAlerta("Error", "No se pudo guardar la nota: " + ex.getMessage());
            }
        });

        btnCancelar.setOnAction(e -> ventana.setScene(escenaPrincipal)); // Vuelve a la escena principal sin guardar cambios
        Scene escenaFormulario = new Scene(panelFormulario, 450, 400);
        escenaFormulario.getStylesheets().add(getClass().getResource("estilos.css").toExternalForm());
        ventana.setScene(escenaFormulario);
    }

    private void eliminarNotaSeleccionada() { // Método para eliminar la nota seleccionada
        if (notaSeleccionada == null) {
            mostrarAlerta("Aviso", "Selecciona primero la nota que quieres borrar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar borrado");
        confirmacion.setHeaderText("¿Borrar \"" + notaSeleccionada.getTitulo() + "\"?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        confirmacion.getDialogPane().getStylesheets().add(rutaCss); // Vincula el CSS al diálogo de confirmación

        Optional<ButtonType> resultado = confirmacion.showAndWait(); //contenedor que guarda el resultado de la acción del usuario (OK o Cancelar)
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                gestor.borrarFichero(notaSeleccionada.getNombreFichero());
                notaSeleccionada = null;
                txtVisualizacionNota.clear();
                actualizarListaNotas(null);
            } catch (IOException ex) {
                mostrarAlerta("Error", "No se pudo borrar: " + ex.getMessage());
            }
        }
    }

    private void buscarNotas(String texto) {
        try {
            List<Nota> todas = gestor.cargarNotas();
            List<Nota> filtradas = gestor.filtrarNotas(todas, texto);
            actualizarListaNotas(filtradas);
        } catch (IOException ex) {
            mostrarAlerta("Error", "Error al buscar notas: " + ex.getMessage());
        }
    }

    private void actualizarListaNotas(List<Nota> notasAMostrar) { // Actualiza la lista de notas mostradas en el panel izquierdo. Si notasAMostrar es null, se cargan todas las notas.
        panelBotonesNotas.getChildren().clear();
        try {
            List<Nota> lista;

            if (notasAMostrar != null) {
                lista = notasAMostrar;
            } else {
                lista = gestor.cargarNotas();
            }

            for (Nota n : lista) {
                Button btnNota = new Button(n.getTitulo() + "\n" + n.getCategoria());
                btnNota.setMaxWidth(Double.MAX_VALUE);
                btnNota.getStyleClass().add("tarjeta-nota");
                btnNota.setOnAction(e -> {
                    notaSeleccionada = n;
                    txtVisualizacionNota.setText(n.toString());
                });
                panelBotonesNotas.getChildren().add(btnNota);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar notas: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) { // Alertas a mostrar cuando ocurre un error o se necesita informar al usuario de algo importante
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.getDialogPane().getStylesheets().add(rutaCss);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();

    }

    public static void main(String[] args) {
        launch();
    }
}
