package com.example.demo.controllers;

import com.example.demo.models.Libro;
import com.example.demo.models.Persona;
import com.example.demo.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/libros")
public class LibroController {

    private final LibroService libroService;

    @Autowired
    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public ResponseEntity<List<Libro>> getAllLibros() {
        return ResponseEntity.ok(libroService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> getLibroById(@PathVariable Long id) {
        return libroService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Libro> createLibro(@RequestBody Libro libro) {
        return new ResponseEntity<>(libroService.save(libro), org.springframework.http.HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> updateLibro(@PathVariable Long id, @RequestBody Libro libro) {
        return libroService.findById(id)
                .map(existingLibro -> {
                    if (libro.getTitulo() != null) {
                        existingLibro.setTitulo(libro.getTitulo());
                    }
                    if (libro.getAutor() != null) {
                        existingLibro.setAutor(libro.getAutor());
                    }
                    if (libro.getAnioPublicado() != null) {
                        existingLibro.setAnioPublicado(libro.getAnioPublicado());
                    }
                    if (libro.getDisponible() != null) {
                        existingLibro.setDisponible(libro.getDisponible());
                    }
                    return ResponseEntity.ok(libroService.save(existingLibro));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Optional<ResponseEntity<Void>> deleteLibro(@PathVariable Long id) {
        return Optional.of(libroService.findById(id)
                .map(libro -> {
                    libroService.deleteById(id);
                    return new ResponseEntity<Void>(org.springframework.http.HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build()));
    }


}
