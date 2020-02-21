package com.cda.micro.repository;

import com.cda.micro.domain.Subcategoria;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Subcategoria entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {

}
