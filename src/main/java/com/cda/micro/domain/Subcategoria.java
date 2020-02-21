package com.cda.micro.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Subcategoria.
 */
@Entity
@Table(name = "sub_categoria")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Subcategoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 100)
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @OneToMany(mappedBy = "subcategoria")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Caracteristica> caracteristicas = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("subcategorias")
    private Categoria categoria;

   

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Subcategoria descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Caracteristica> getCaracteristicas() {
        return caracteristicas;
    }

    public Subcategoria caracteristicas(Set<Caracteristica> caracteristicas) {
        this.caracteristicas = caracteristicas;
        return this;
    }

    public Subcategoria addCaracteristicas(Caracteristica caracteristica) {
        this.caracteristicas.add(caracteristica);
        caracteristica.setSubcategoria(this);
        return this;
    }

    public Subcategoria removeCaracteristicas(Caracteristica caracteristica) {
        this.caracteristicas.remove(caracteristica);
        caracteristica.setSubcategoria(null);
        return this;
    }

    public void setCaracteristicas(Set<Caracteristica> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Subcategoria categoria(Categoria categoria) {
        this.categoria = categoria;
        return this;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

   
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subcategoria)) {
            return false;
        }
        return id != null && id.equals(((Subcategoria) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Subcategoria{" +
            "id=" + getId() +
            ", descripcion='" + getDescripcion() + "'" +
            "}";
    }
}
