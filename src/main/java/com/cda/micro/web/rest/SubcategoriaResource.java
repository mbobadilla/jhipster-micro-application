package com.cda.micro.web.rest;

import com.cda.micro.domain.Subcategoria;
import com.cda.micro.repository.SubcategoriaRepository;
import com.cda.micro.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.cda.micro.domain.Subcategoria}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SubcategoriaResource {

    private final Logger log = LoggerFactory.getLogger(SubcategoriaResource.class);

    private static final String ENTITY_NAME = "jhipsterMicroservicesSubcategoria";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubcategoriaRepository subcategoriaRepository;

    public SubcategoriaResource(SubcategoriaRepository subcategoriaRepository) {
        this.subcategoriaRepository = subcategoriaRepository;
    }

    /**
     * {@code POST  /subcategorias} : Create a new subcategoria.
     *
     * @param subcategoria the subcategoria to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subcategoria, or with status {@code 400 (Bad Request)} if the subcategoria has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/subcategorias")
    public ResponseEntity<Subcategoria> createSubcategoria(@Valid @RequestBody Subcategoria subcategoria) throws URISyntaxException {
        log.debug("REST request to save Subcategoria : {}", subcategoria);
        if (subcategoria.getId() != null) {
            throw new BadRequestAlertException("A new subcategoria cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Subcategoria result = subcategoriaRepository.save(subcategoria);
        return ResponseEntity.created(new URI("/api/subcategorias/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /subcategorias} : Updates an existing subcategoria.
     *
     * @param subcategoria the subcategoria to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategoria,
     * or with status {@code 400 (Bad Request)} if the subcategoria is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subcategoria couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/subcategorias")
    public ResponseEntity<Subcategoria> updateSubcategoria(@Valid @RequestBody Subcategoria subcategoria) throws URISyntaxException {
        log.debug("REST request to update Subcategoria : {}", subcategoria);
        if (subcategoria.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Subcategoria result = subcategoriaRepository.save(subcategoria);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subcategoria.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /subcategorias} : get all the subcategorias.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subcategorias in body.
     */
    @GetMapping("/subcategorias")
    public List<Subcategoria> getAllSubcategorias() {
        log.debug("REST request to get all Subcategorias");
        return subcategoriaRepository.findAll();
    }

    /**
     * {@code GET  /subcategorias/:id} : get the "id" subcategoria.
     *
     * @param id the id of the subcategoria to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subcategoria, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/subcategorias/{id}")
    public ResponseEntity<Subcategoria> getSubcategoria(@PathVariable Long id) {
        log.debug("REST request to get Subcategoria : {}", id);
        Optional<Subcategoria> subcategoria = subcategoriaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(subcategoria);
    }

    /**
     * {@code DELETE  /subcategorias/:id} : delete the "id" subcategoria.
     *
     * @param id the id of the subcategoria to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/subcategorias/{id}")
    public ResponseEntity<Void> deleteSubcategoria(@PathVariable Long id) {
        log.debug("REST request to delete Subcategoria : {}", id);
        subcategoriaRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
