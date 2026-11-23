package com.example;

public class Nota {
    java.time.LocalDateTime fecha;
    String titulo;
    String categoria;
    String contenido;
    String nombreFichero;

    public Nota( String titulo, String categoria, String contenido) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.contenido = contenido;
    }
    
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

    @Override 
    public String toString() {
        return "título: " 
               + titulo + "\n" +
               "Categoría: " + categoria + "\n" + "---\n" +
               contenido;
    }
}
