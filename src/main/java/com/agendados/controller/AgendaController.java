package com.agendados.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import com.agendados.model.Contacto;

import com.agendados.service.ContactoService;

@Controller
@RequestMapping("/agenda")
public class AgendaController {
    private final ContactoService contactoService;

    public AgendaController(ContactoService contactoService){
        this.contactoService = contactoService;
    }

    @GetMapping("")
    public String mostrarContacto(Model model, @ModelAttribute("message") String mensaje){
        model.addAttribute("agenda", contactoService.obtenerContacto());
        model.addAttribute("message", mensaje);
        return "agenda";
    }

    @GetMapping("/nuevo")
    public String nuevoContactoForm(Model model){
        model.addAttribute("contacto", new Contacto());
        return "nuevoContacto";
    }

    @PostMapping("/guardar")
    public String guardarContacto(@ModelAttribute Contacto contacto, RedirectAttributes redirectAttributes){
        contactoService.guardarContacto(contacto);
        redirectAttributes.addFlashAttribute("guardar", "Contacto guardar con éxito");
        return "redirect:/agenda";
    }

    @PostMapping("/eliminar/{nombre}")
    public String eliminarContacto(@PathVariable("nombre") String nombre, RedirectAttributes redirectAttributes){
        boolean eliminado = contactoService.eliminarContacto(nombre);
        if(eliminado){
            redirectAttributes.addAttribute("message", "Contacto eliminado con éxito");
        }else{
            redirectAttributes.addFlashAttribute("message", "No se pudo eliminar un contacto");
        }
        return "redirect:/agenda";
    }

    @GetMapping("/editar/{nombre}")
    public String editarContacto(@PathVariable("nombre") String nombre, Model model){
        Contacto contacto = contactoService.obtenerContactoPorNombre(nombre);
        model.addAttribute("contacto", contacto);
        return "editarContacto";
    }

    @PostMapping("/actualizar/{nombre}")
    public String actualizarContacto(@PathVariable("nombre") String nombreOriginal, Contacto contactoActualizado, RedirectAttributes redirectAttributes){
        contactoService.actualizarContacto(nombreOriginal, contactoActualizado);
        redirectAttributes.addFlashAttribute("message", "Contacto actualizado con éxito");
        return "redirect:/agenda";
    }

    @PostMapping("/favorito/{nombre}")
    public String cambiarFavorito(@PathVariable("nombre") String nombre, RedirectAttributes redirectAttributes){
        contactoService.cambiarFavorito(nombre);
        redirectAttributes.addFlashAttribute("message", "Estado de favorito actualizado");
        return "redirect:/agenda";
    }
}
