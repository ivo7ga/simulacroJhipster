package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Avion;
import com.mycompany.myapp.domain.Vuelo;
import com.mycompany.myapp.service.AvionServiceSimulacro;
import com.mycompany.myapp.service.VueloServiceSimulacro;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SimulacroMyController {

    VueloServiceSimulacro vueloMyServiceSimulacro;
    AvionServiceSimulacro avionMyServiceSimulacro;

    public SimulacroMyController(VueloServiceSimulacro vueloMyServiceSimulacro, AvionServiceSimulacro avionMyServiceSimulacro) {
        this.vueloMyServiceSimulacro = vueloMyServiceSimulacro;
        this.avionMyServiceSimulacro = avionMyServiceSimulacro;
    }

    //metrica 1
    @GetMapping("/avion")
    public ResponseEntity<Optional<Avion>> getAvionViejo() {
        return ResponseEntity.ok(avionMyServiceSimulacro.getAvionViejo());
    }

    //metrica 2
    @GetMapping("/vuelo/pilotos")
    public ResponseEntity<Page<Vuelo>> findByPiloto_dni(
        @RequestParam String dni,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        if (validarDni(dni)) return ResponseEntity.ok(vueloMyServiceSimulacro.findByPiloto_Dni(dni, pageable));
        return new ResponseEntity("Mal", HttpStatus.BAD_REQUEST);
    }

    //metrica 3
    @GetMapping("/vuelo/tripulantes")
    public ResponseEntity<Long> countByTripulantes_Dni(@RequestParam String dni) {
        if (validarDni(dni)) return ResponseEntity.ok(vueloMyServiceSimulacro.countByTripulantes_Dni(dni));
        return new ResponseEntity("Mal", HttpStatus.BAD_REQUEST);
    }

    public boolean validarDni(String dni) {
        if (dni.matches("[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]")) return true;
        return false;
    }
}
