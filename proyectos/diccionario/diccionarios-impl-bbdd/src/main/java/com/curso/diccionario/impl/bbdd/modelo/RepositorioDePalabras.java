package com.curso.diccionario.impl.bbdd.modelo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioDePalabras extends JpaRepository<PalabraEntidad, Long> {

    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    Optional<PalabraEntidad> findByIdiomaAndTexto(String idioma, String texto);

    boolean existsByIdiomaAndTexto(String idioma, String texto);


    // Solo los textos, no las entidades con sus significados: el servicio los quiere para comparar.
    @Query("select p.texto from PalabraEntidad p where p.idioma = :idioma order by p.texto")
    List<String> textosDe(@Param("idioma") String idioma);

    // Borrado en bloque y no deleteAll(findByIdioma(...)): eso cargaría las 5000 palabras con sus
    // significados y ejemplos para borrarlas una a una. Tres sentencias, de hijos a padres.
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = """
            delete from ejemplos where significado_id in (
                select s.id from significados s join palabras p on s.palabra_id = p.id where p.idioma = :idioma)""")
    void borrarEjemplosDe(@Param("idioma") String idioma);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "delete from significados where palabra_id in (select id from palabras where idioma = :idioma)")
    void borrarSignificadosDe(@Param("idioma") String idioma);

    @Modifying(clearAutomatically = true)
    @Query("delete from PalabraEntidad p where p.idioma = :idioma")
    void borrarPalabrasDe(@Param("idioma") String idioma);
}
