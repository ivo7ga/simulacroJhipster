package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Aeropuerto.
 */
@Entity
@Table(name = "aeropuerto")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Aeropuerto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 10, max = 255)
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @NotNull
    @Size(min = 10, max = 255)
    @Column(name = "ciudad", length = 255, nullable = false)
    private String ciudad;

    @OneToMany(mappedBy = "aeropuerto")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "aeropuerto", "destinoAeropuerto", "avion", "piloto", "tripulantes" }, allowSetters = true)
    private Set<Vuelo> vuelos = new HashSet<>();

    @OneToMany(mappedBy = "destinoAeropuerto")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "aeropuerto", "destinoAeropuerto", "avion", "piloto", "tripulantes" }, allowSetters = true)
    private Set<Vuelo> vueloDestinos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Aeropuerto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Aeropuerto nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return this.ciudad;
    }

    public Aeropuerto ciudad(String ciudad) {
        this.setCiudad(ciudad);
        return this;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Set<Vuelo> getVuelos() {
        return this.vuelos;
    }

    public void setVuelos(Set<Vuelo> vuelos) {
        if (this.vuelos != null) {
            this.vuelos.forEach(i -> i.setAeropuerto(null));
        }
        if (vuelos != null) {
            vuelos.forEach(i -> i.setAeropuerto(this));
        }
        this.vuelos = vuelos;
    }

    public Aeropuerto vuelos(Set<Vuelo> vuelos) {
        this.setVuelos(vuelos);
        return this;
    }

    public Aeropuerto addVuelo(Vuelo vuelo) {
        this.vuelos.add(vuelo);
        vuelo.setAeropuerto(this);
        return this;
    }

    public Aeropuerto removeVuelo(Vuelo vuelo) {
        this.vuelos.remove(vuelo);
        vuelo.setAeropuerto(null);
        return this;
    }

    public Set<Vuelo> getVueloDestinos() {
        return this.vueloDestinos;
    }

    public void setVueloDestinos(Set<Vuelo> vuelos) {
        if (this.vueloDestinos != null) {
            this.vueloDestinos.forEach(i -> i.setDestinoAeropuerto(null));
        }
        if (vuelos != null) {
            vuelos.forEach(i -> i.setDestinoAeropuerto(this));
        }
        this.vueloDestinos = vuelos;
    }

    public Aeropuerto vueloDestinos(Set<Vuelo> vuelos) {
        this.setVueloDestinos(vuelos);
        return this;
    }

    public Aeropuerto addVueloDestino(Vuelo vuelo) {
        this.vueloDestinos.add(vuelo);
        vuelo.setDestinoAeropuerto(this);
        return this;
    }

    public Aeropuerto removeVueloDestino(Vuelo vuelo) {
        this.vueloDestinos.remove(vuelo);
        vuelo.setDestinoAeropuerto(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Aeropuerto)) {
            return false;
        }
        return id != null && id.equals(((Aeropuerto) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Aeropuerto{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", ciudad='" + getCiudad() + "'" +
            "}";
    }
}
