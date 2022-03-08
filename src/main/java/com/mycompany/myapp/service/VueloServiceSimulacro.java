package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Vuelo;
import com.mycompany.myapp.repository.VueloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VueloServiceSimulacro {

    @Autowired
    VueloRepository vueloRepository;

    public Page<Vuelo> findByPiloto_Dni(String dni, Pageable pageable) {
        return vueloRepository.findByPiloto_Dni(dni, pageable);
    }

    public Long countByTripulantes_Dni(String dni) {
        return vueloRepository.countByTripulantes_Dni(dni);
    }
}
