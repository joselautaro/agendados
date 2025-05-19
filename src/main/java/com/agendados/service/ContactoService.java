package com.agendados.service;

// Importación de estructuras de datos necesarias
import java.util.HashMap;
import java.util.Map;

// Indica que esta clase es un componente de servicio en Spring
import org.springframework.stereotype.Service;

import com.agendados.model.Contacto;

// Clase de servicio que maneja la lógica de negocios relacionada con contactos
@Service
public class ContactoService {

    // Mapa que actúa como una "base de datos en memoria" con nombre como clave y el objeto Contacto como valor
    private final Map<String, Contacto> agenda = new HashMap<>();

    // Método que retorna todos los contactos almacenados
    public Map<String, Contacto> obtenerContacto() {
        return agenda;
    }

    // Método que elimina un contacto si existe y no es favorito
    public boolean eliminarContacto(String nombre) {
        // Obtiene el contacto por su nombre
        Contacto contacto = agenda.get(nombre);
        // Si existe y no es favorito, se elimina
        if (contacto != null && !contacto.isFavorito()) {
            agenda.remove(nombre); // Elimina el contacto del mapa
            return true; // Indica que fue eliminado con éxito
        }
        return false; // Si es favorito o no existe, no se elimina
    }

    // Retorna un contacto específico por su nombre
    public Contacto obtenerContactoPorNombre(String nombre) {
        return agenda.get(nombre); // Devuelve el contacto desde el mapa
    }

    // Actualiza un contacto existente
    public void actualizarContacto(String nombreOriginal, Contacto contactoActualizado) {
        // Si el nombre cambió, se elimina la entrada anterior
        if (!nombreOriginal.equals(contactoActualizado.getNombre())) {
            agenda.remove(nombreOriginal);
        }
        // Se guarda o reemplaza el contacto actualizado
        agenda.put(contactoActualizado.getNombre(), contactoActualizado);
    }

    // Cambia el estado de favorito de un contacto (true ↔ false)
    public void cambiarFavorito(String nombre) {
        Contacto contacto = agenda.get(nombre); // Busca el contacto por nombre
        if (contacto != null) {
            contacto.setFavorito(!contacto.isFavorito()); // Invierte el valor booleano actual
        }
    }

    // Guarda un nuevo contacto en la agenda
    public void guardarContacto(Contacto contacto) {
        agenda.put(contacto.getNombre(), contacto); // Usa el nombre como clave
    }

    // Filtra contactos que sean favoritos o no, según el parámetro recibido
    public Map<String, Contacto> obtenerFavoritos(boolean soloFavoritos) {
        Map<String, Contacto> resultado = new HashMap<>(); // Mapa donde se guardarán los resultados filtrados
        for (Map.Entry<String, Contacto> entry : agenda.entrySet()) {
            // Si el estado favorito coincide con el parámetro, se agrega al resultado
            if (entry.getValue().isFavorito() == soloFavoritos) {
                resultado.put(entry.getKey(), entry.getValue());
            }
        }
        return resultado; // Devuelve el nuevo mapa filtrado
    }
}

