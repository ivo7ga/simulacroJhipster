package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Avion;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Avion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AvionRepository extends JpaRepository<Avion, Long>, JpaSpecificationExecutor<Avion> {
    //metrica 1
    Optional<Avion> findFirstByOrderByEdadDesc();
}
