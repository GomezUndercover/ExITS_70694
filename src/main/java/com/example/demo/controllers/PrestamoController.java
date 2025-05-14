package com.example.demo.controllers;

import com.example.demo.models.Prestamo;
import com.example.demo.models.Persona;
import com.example.demo.models.Libro;
import com.example.demo.services.PrestamoService;
import com.example.demo.services.PersonaService;
import com.example.demo.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final PersonaService personaService;
    private final LibroService libroService;

    @Autowired
    public PrestamoController(PrestamoService prestamoService, PersonaService personaService, LibroService libroService) {
        this.prestamoService = prestamoService;
        this.personaService = personaService;
        this.libroService = libroService;
    }

    @PostMapping
    public ResponseEntity<?> solicitarPrestamos(@RequestBody Map<String, Object> payload) {
        Long personaId = ((Number) payload.get("personaId")).longValue();
        List<Integer> libroIds = (List<Integer>) payload.get("libros");

        if (personaId == null || libroIds == null || libroIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Se requieren personaId y al menos un libroId");
        }

        try {
            Persona persona = personaService.findById(personaId)
                    .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));


            List<Prestamo> prestamosActivos = prestamoService.findPrestamosActivos(persona);
            List<Long> librosPrestados = prestamosActivos.stream()
                    .map(prestamo -> prestamo.getLibro().getId())
                    .collect(Collectors.toList());

            List<Prestamo> nuevosPrestamos = new ArrayList<>();
            for (Integer id : libroIds) {
                Long libroId = id.longValue();

                if (librosPrestados.contains(libroId)) {
                    throw new IllegalStateException("La persona ya tiene prestado el libro con ID: " + libroId);
                }

                Libro libro = libroService.findById(libroId)
                        .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con id: " + id));

                if (!libro.getDisponible()) {
                    throw new IllegalStateException("El libro con ID " + libroId + " no está disponible");
                }

                Prestamo prestamo = prestamoService.prestarLibro(persona, libro);
                nuevosPrestamos.add(prestamo);
            }

            return new ResponseEntity<>(nuevosPrestamos, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/personas/{id}/prestamos")
    public ResponseEntity<List<Prestamo>> getPrestamosByPersona(@PathVariable Long id) {
        return personaService.findById(id)
                .map(persona -> ResponseEntity.ok(prestamoService.findPrestamosActivos(persona)))
                .orElse(ResponseEntity.notFound().build());
    }
}