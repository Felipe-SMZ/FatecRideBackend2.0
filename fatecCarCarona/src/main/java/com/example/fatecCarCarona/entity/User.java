package com.example.fatecCarCarona.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
 	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private String telefone;
    private String foto;

    @ManyToOne
    @JoinColumn(name = "id_tipo_usuario")
    private UserType userType;

    @ManyToOne
    @JoinColumn(name = "id_genero")
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "id_curso")
    private Course course;

}
