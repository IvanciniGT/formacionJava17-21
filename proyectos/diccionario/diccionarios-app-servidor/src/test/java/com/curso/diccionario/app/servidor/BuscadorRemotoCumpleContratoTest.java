package com.curso.diccionario.app.servidor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.curso.diccionario.api.contrato.DatosDePrueba;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.contrato.ContratoBuscadorDePalabrasTest;
import com.curso.diccionario.service.rest.BuscadorDePalabrasRemoto;

// El servicio remoto (el de la app de consola con -Premoto) contra este servidor de verdad, con el
// contrato del servicio: las mismas pruebas que pasa el servicio local. Si pasa, servicio remoto +
// HTTP + controlador + servicio del servidor + repositorio se comportan igual que el servicio local:
// quien lo use no nota que hay una red por medio.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = { ServidorDeDiccionarios.class, ServidorConDatosDePrueba.class },
        properties = {
                // El servicio remoto contra el servidor, con datos de prueba puestos a mano: ni el
                // repositorio en BBDD ni el cargador, que leería los jars de idioma.
                "spring.autoconfigure.exclude=com.curso.diccionario.impl.bbdd.AutoConfiguracionDeDiccionariosEnBBDD,com.curso.diccionario.cargador.AutoConfiguracionDelCargador",
                "spring.datasource.url=jdbc:h2:mem:contrato" })
class BuscadorRemotoCumpleContratoTest extends ContratoBuscadorDePalabrasTest {

    @Value("${local.server.port}")
    int puerto;

    @Autowired
    ServidorConDatosDePrueba.SuministradorIntercambiable enElServidor;

    @Override
    protected BuscadorDePalabras crearBuscadorCon(DatosDePrueba datos) {
        enElServidor.usar(datos);
        return new BuscadorDePalabrasRemoto("http://localhost:" + puerto);
    }
}
