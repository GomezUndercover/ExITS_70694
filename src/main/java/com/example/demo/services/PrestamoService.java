package com.example.demo.services;

import com.example.demo.models.Prestamo;
import com.example.demo.models.Persona;
import com.example.demo.models.Libro;
import com.example.demo.repositories.PrestamoRepository;
import com.example.demo.repositories.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    @Autowired
    public PrestamoService(PrestamoRepository prestamoRepository, LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
    }

    public List<Prestamo> findAll() {
        return prestamoRepository.findAll();
    }

    public Optional<Prestamo> findById(Long id) {
        return prestamoRepository.findById(id);
    }

    public List<Prestamo> findByPersona(Persona persona) {
        return prestamoRepository.findByPersona(persona);
    }

    public List<Prestamo> findPrestamosActivos(Persona persona) {
        return prestamoRepository.findByPersonaAndDevueltoFalse(persona);
    }


    // la etiqueta @Transactional nos sirve para poder asegurarnos que
    // la operacion se ejecuta en una sola transaccion, lo que significa que
    // si falla, no se hara ninguna operacion, esto para mantener integridad de datos

    @Transactional
    // este metodo realiza la prestacion del libro, primero verificando si esta disponible o no
    // luego se realiza la proceso de prestar el libro, asignando quien lo esta prestando y marcando como no disponible
    // despues, se guarda el libro con los atributos modificados
    public Prestamo prestarLibro(Persona persona, Libro libro) {
        if (!libro.getDisponible()) {
            throw new IllegalStateException("El libro lo esta prestando otra persona en este momento, mil disculpas");
        }
        Prestamo prestamo = new Prestamo();
        prestamo.setPersona(persona);
        prestamo.setLibro(libro);
        libro.setDisponible(false);
        prestamo.setFechaPrestamo(LocalDate.now());
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }

    @Transactional
    // el metodo de devolver primero checa si el prestamo que se esta solicitando existe o si ya fue devuelto.
    // despues, se marca el libro como devuelto, y se le asigna los atributos necesarios como fecha devuelta
    // y set disponible, luego se guarda el libro.
    public Prestamo devolverLibro(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new IllegalArgumentException("Prestamo no encontrado"));

        if (prestamo.getDevuelto()) {
            throw new IllegalStateException("El libro ya ha sido devuelto");
        }

        // marcar devuelto libro y volver disponible
        prestamo.setDevuelto(true);
        prestamo.setFechaDevolucion(LocalDate.now());
        Libro libro = prestamo.getLibro();
        libro.setDisponible(true);
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }
}