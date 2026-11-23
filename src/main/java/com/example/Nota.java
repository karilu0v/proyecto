package com.example;

public class Nota {
    // Atributos de la clase Nota
    java.time.LocalDateTime fecha;
    String titulo;
    String categoria;
    String contenido;
    String nombreFichero; // Atributo para almacenar el nombre del fichero asociado a la nota el cual se obtiene a partir de la fecha de creación de la nota en el formato MMddyyHHmmss.txt

    // Constructor que recibe el título, categoría y contenido de la nota

    public Nota( String titulo, String categoria, String contenido) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.contenido = contenido;
    }
    
    // Getters y setters para los atributos de la clase Nota
    public String getNombreFichero() {
        return nombreFichero;
    }
    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }
    public java.time.LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(java.time.LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    // Método toString que devuelve una representación en cadena de la nota, incluyendo el título, categoría y contenido
    @Override 
    public String toString() {
        return "título: " 
               + titulo + "\n" +
               "Categoría: " + categoria + "\n" + "---\n" +
               contenido;
    }
}
