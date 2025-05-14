package com.example.demo.repositories;

import com.example.demo.models.Prestamo;
import com.example.demo.models.Persona;
import com.example.demo.models.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByPersona(Persona persona);
    List<Prestamo> findByLibro(Libro libro);
    List<Prestamo> findByPersonaAndDevueltoFalse(Persona persona);
}