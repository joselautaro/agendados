package com.agendados.model;

// Importación de clases para manejar listas
import java.util.ArrayList;
import java.util.List;

// Clase que representa un contacto en la agenda
public class Contacto {

    // Atributo que almacena el nombre del contacto
    private String nombre;

    // Atributo que almacena el número de teléfono del contacto
    private String telefono;

    // Atributo que almacena el email del contacto
    private String email;

    // Atributo que indica si el contacto está marcado como favorito
    private boolean favorito;

    // Atributo que almacena las etiquetas asociadas al contacto (por ejemplo: familia, trabajo, etc.)
    private List<String> etiquetas = new ArrayList<>(); // Se inicializa con una lista vacía por defecto

    // Constructor completo que inicializa todos los atributos, incluidas las etiquetas
    public Contacto(String nombre, String telefono, String email, boolean favorito, List<String> etiquetas) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.favorito = favorito;
        // Si etiquetas es null, se asigna una lista vacía para evitar errores
        this.etiquetas = etiquetas != null ? etiquetas : new ArrayList<>();
    }

    // Constructor alternativo que no requiere etiquetas (las inicializa como lista vacía)
    public Contacto(String nombre, String telefono, String email, boolean favorito) {
        // Se llama al constructor principal, pasando una nueva lista vacía como etiquetas
        this(nombre, telefono, email, favorito, new ArrayList<>());
    }

    // Constructor vacío (requerido por frameworks como Spring para autocompletar formularios)
    public Contacto() {}

    // Métodos getter y setter para acceder y modificar los atributos

    // Retorna el nombre del contacto
    public String getNombre() {
        return nombre;
    }

    // Asigna un nuevo nombre al contacto
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Retorna el número de teléfono del contacto
    public String getTelefono() {
        return telefono;
    }

    // Asigna un nuevo número de teléfono al contacto
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Retorna el email del contacto
    public String getEmail() {
        return email;
    }

    // Asigna un nuevo email al contacto
    public void setEmail(String email) {
        this.email = email;
    }

    // Retorna si el contacto es favorito (true o false)
    public boolean isFavorito() {
        return favorito;
    }

    // Cambia el estado de favorito del contacto
    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    // Retorna la lista de etiquetas asociadas al contacto
    public List<String> getEtiquetas() {
        return etiquetas;
    }

    // Asigna una nueva lista de etiquetas al contacto (si es null, se usa una lista vacía)
    public void setEtiquetas(List<String> etiquetas) {
        this.etiquetas = etiquetas != null ? etiquetas : new ArrayList<>();
    }
}
