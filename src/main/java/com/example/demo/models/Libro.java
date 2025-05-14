package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "libros")
@Getter
@Setter
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Explicitly map this column
    private Long id;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo; // Specify the next column

    @Column(name = "autor", nullable = false, length = 255)
    private String autor;

    @Column(name = "anio_publicado", nullable = false)
    private Integer anioPublicado;

    @Column(name = "disponible", nullable = false)
    private Boolean disponible = true;
}