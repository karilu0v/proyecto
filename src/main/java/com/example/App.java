package com.example;

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

        columna.getChildren().addAll( boton, etiqueta, boton2);

        Scene escena = new Scene(columna, 600, 400);

        ventana.setScene(escena);

        ventana.show();
    }


    public static void main(String[] args) {
        launch();
    }

}