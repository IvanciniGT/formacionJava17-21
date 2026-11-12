package com.curso.diccionario.controlador.rest.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

// 1.1.0: gana las sugerencias, solo para PALABRA_NO_ENCONTRADA. Un campo nuevo en una respuesta no
// rompe a quien ya la leía (lo ignora): minor. En los demás errores no aparece (NON_NULL), para no
// decir "sugerencias: null" donde no pinta nada.
//
// Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
@Schema(description = "Cuerpo de todas las respuestas de error")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDTO(
        @Schema(description = "Motivo del error", requiredMode = Schema.RequiredMode.REQUIRED)
        CodigoDeError codigo,
        @Schema(description = "Explicación para personas; no está pensada para que la procese un programa",
                example = "No hay diccionario para el idioma XX", requiredMode = Schema.RequiredMode.REQUIRED)
        String mensaje,
        @ArraySchema(arraySchema = @Schema(description = "Solo en PALABRA_NO_ENCONTRADA: las palabras del diccionario "
                + "parecidas a la buscada, de la más a la menos parecida (puede venir vacía)"),
                schema = @Schema(example = "mañana"))
        List<String> sugerencias) {

    // El de la 1.0.0, para los errores que no llevan sugerencias.
    public ErrorDTO(CodigoDeError codigo, String mensaje) {
        this(codigo, mensaje, null);
    }
}
