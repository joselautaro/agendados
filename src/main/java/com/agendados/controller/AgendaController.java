package com.agendados.controller;

// Importación de clases necesarias para manejo de colecciones, mapeo, y utilidades de stream
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.agendados.model.Contacto;
import com.agendados.service.ContactoService;

// Indica que esta clase es un controlador de Spring
@Controller
// Define el prefijo para todas las rutas de este controlador
@RequestMapping("/agenda")
public class AgendaController {

    // Inyección del servicio que gestiona los contactos
    private final ContactoService contactoService;

    // Constructor para inyectar el servicio mediante constructor
    public AgendaController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    // Método que maneja las solicitudes GET a "/agenda"
    @GetMapping("")
    public String mostrarContacto(
            @ModelAttribute("message") String mensaje, // Mensaje de éxito/error opcional
            @RequestParam(name = "filtro", required = false) String filtro, // Filtro por favorito/no favorito
            @RequestParam(name = "busqueda", required = false) String busqueda, // Texto a buscar
            @RequestParam(name = "orden", required = false) String orden, // Orden ascendente o descendente
            @RequestParam(name = "etiqueta", required = false) String etiqueta, // Etiqueta para filtrar
            Model model) { // Modelo para pasar datos a la vista

        Map<String, Contacto> contactos;

        // Filtrado por tipo usando switch moderno (Java 14+)
        switch (filtro != null ? filtro : "") {
            case "favoritos" -> contactos = contactoService.obtenerFavoritos(true); // Solo favoritos
            case "no-favoritos" -> contactos = contactoService.obtenerFavoritos(false); // Solo no favoritos
            default -> contactos = contactoService.obtenerContacto(); // Todos los contactos
        }

        // Si se ingresó una búsqueda por nombre
        if (busqueda != null && !busqueda.isEmpty()) {
            Map<String, Contacto> filtrados = new HashMap<>();
            for (Map.Entry<String, Contacto> entry : contactos.entrySet()) {
                // Comparación ignorando mayúsculas/minúsculas
                if (entry.getKey().toLowerCase().contains(busqueda.toLowerCase())) {
                    filtrados.put(entry.getKey(), entry.getValue());
                }
            }
            contactos = filtrados;
        }

        // Si se está filtrando por etiqueta
        if (etiqueta != null && !etiqueta.isBlank()) {
            Map<String, Contacto> filtradosPorEtiqueta = new HashMap<>();
            for (Map.Entry<String, Contacto> entry : contactos.entrySet()) {
                // Verifica si la lista de etiquetas contiene la etiqueta buscada
                if (entry.getValue().getEtiquetas() != null &&
                        entry.getValue().getEtiquetas().contains(etiqueta)) {
                    filtradosPorEtiqueta.put(entry.getKey(), entry.getValue());
                }
            }
            contactos = filtradosPorEtiqueta;
        }

        // Orden ascendente por nombre
        if ("asc".equalsIgnoreCase(orden)) {
            contactos = contactos.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // Ordena por nombre A-Z
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldVal, newVal) -> oldVal,
                            LinkedHashMap::new)); // Mantiene el orden de inserción
        }
        // Orden descendente por nombre
        else if ("desc".equalsIgnoreCase(orden)) {
            contactos = contactos.entrySet().stream()
                    .sorted(Map.Entry.<String, Contacto>comparingByKey().reversed()) // Ordena por nombre Z-A
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldVal, newVal) -> oldVal,
                            LinkedHashMap::new));
        }

        // Agrega los datos al modelo para la vista
        model.addAttribute("agenda", contactos);
        model.addAttribute("message", mensaje);
        model.addAttribute("filtro", filtro);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("orden", orden);
        model.addAttribute("etiqueta", etiqueta);
        return "agenda"; // Retorna la vista "agenda.html"
    }

    // Muestra el formulario para agregar un nuevo contacto
    @GetMapping("/nuevo")
    public String nuevoContactoForm(Model model) {
        model.addAttribute("contacto", new Contacto()); // Objeto vacío para rellenar en el formulario
        return "nuevoContacto"; // Vista de creación
    }

    // Guarda un nuevo contacto en memoria
    @PostMapping("/guardar")
    public String guardarContacto(@ModelAttribute Contacto contacto,
                                   @RequestParam(name = "etiquetasTexto", required = false) String etiquetasTexto,
                                   RedirectAttributes redirectAttributes) {
        contacto.setEtiquetas(parsearEtiquetas(etiquetasTexto)); // Convierte el texto en lista
        contactoService.guardarContacto(contacto); // Guarda el contacto
        redirectAttributes.addFlashAttribute("guardar", "Contacto guardado con éxito"); // Mensaje flash
        return "redirect:/agenda"; // Redirecciona a la lista
    }

    // Elimina un contacto por nombre si no es favorito
    @PostMapping("/eliminar/{nombre}")
    public String eliminarContacto(@PathVariable("nombre") String nombre, RedirectAttributes redirectAttributes) {
        boolean eliminado = contactoService.eliminarContacto(nombre); // Intenta eliminar
        if (eliminado) {
            redirectAttributes.addAttribute("message", "Contacto eliminado con éxito");
        } else {
            redirectAttributes.addFlashAttribute("message", "No se pudo eliminar el contacto");
        }
        return "redirect:/agenda";
    }

    // Muestra el formulario de edición de un contacto
    @GetMapping("/editar/{nombre}")
    public String editarContacto(@PathVariable("nombre") String nombre, Model model) {
        Contacto contacto = contactoService.obtenerContactoPorNombre(nombre); // Busca por nombre
        model.addAttribute("contacto", contacto);
        return "editarContacto"; // Vista de edición
    }

    // Actualiza los datos del contacto editado
    @PostMapping("/actualizar/{nombre}")
    public String actualizarContacto(@PathVariable("nombre") String nombreOriginal,
                                     @ModelAttribute Contacto contactoActualizado,
                                     @RequestParam(name = "etiquetasTexto", required = false) String etiquetasTexto,
                                     RedirectAttributes redirectAttributes) {
        contactoActualizado.setEtiquetas(parsearEtiquetas(etiquetasTexto)); // Actualiza etiquetas
        contactoService.actualizarContacto(nombreOriginal, contactoActualizado); // Llama al servicio
        redirectAttributes.addFlashAttribute("message", "Contacto actualizado con éxito");
        return "redirect:/agenda";
    }

    // Cambia el estado de favorito del contacto
    @PostMapping("/favorito/{nombre}")
    public String cambiarFavorito(@PathVariable("nombre") String nombre, RedirectAttributes redirectAttributes) {
        contactoService.cambiarFavorito(nombre); // Alterna true/false
        redirectAttributes.addFlashAttribute("message", "Estado de favorito actualizado");
        return "redirect:/agenda";
    }

    // Método auxiliar para convertir un texto separado por comas en lista de strings
    private List<String> parsearEtiquetas(String texto) {
        if (texto == null || texto.isBlank()) return new ArrayList<>(); // Devuelve lista vacía si no hay texto
        return Arrays.stream(texto.split(",")) // Divide por coma
                .map(String::trim) // Elimina espacios
                .filter(s -> !s.isEmpty()) // Elimina vacíos
                .collect(Collectors.toList()); // Crea lista
    }
}
