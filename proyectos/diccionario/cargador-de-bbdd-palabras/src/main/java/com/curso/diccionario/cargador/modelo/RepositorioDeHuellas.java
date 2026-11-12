package com.curso.diccionario.cargador.modelo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioDeHuellas extends JpaRepository<HuellaDeFichero, String> {
}
