package com.cda.micro.web.rest;

import com.cda.micro.domain.Caracteristica;
import com.cda.micro.repository.CaracteristicaRepository;
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
 * REST controller for managing {@link com.cda.micro.domain.Caracteristica}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CaracteristicaResource {

    private final Logger log = LoggerFactory.getLogger(CaracteristicaResource.class);

    private static final String ENTITY_NAME = "jhipsterMicroservicesCaracteristica";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CaracteristicaRepository caracteristicaRepository;

    public CaracteristicaResource(CaracteristicaRepository caracteristicaRepository) {
        this.caracteristicaRepository = caracteristicaRepository;
    }

    /**
     * {@code POST  /caracteristicas} : Create a new caracteristica.
     *
     * @param caracteristica the caracteristica to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new caracteristica, or with status {@code 400 (Bad Request)} if the caracteristica has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/caracteristicas")
    public ResponseEntity<Caracteristica> createCaracteristica(@Valid @RequestBody Caracteristica caracteristica) throws URISyntaxException {
        log.debug("REST request to save Caracteristica : {}", caracteristica);
        if (caracteristica.getId() != null) {
            throw new BadRequestAlertException("A new caracteristica cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Caracteristica result = caracteristicaRepository.save(caracteristica);
        return ResponseEntity.created(new URI("/api/caracteristicas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /caracteristicas} : Updates an existing caracteristica.
     *
     * @param caracteristica the caracteristica to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated caracteristica,
     * or with status {@code 400 (Bad Request)} if the caracteristica is not valid,
     * or with status {@code 500 (Internal Server Error)} if the caracteristica couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/caracteristicas")
    public ResponseEntity<Caracteristica> updateCaracteristica(@Valid @RequestBody Caracteristica caracteristica) throws URISyntaxException {
        log.debug("REST request to update Caracteristica : {}", caracteristica);
        if (caracteristica.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Caracteristica result = caracteristicaRepository.save(caracteristica);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, caracteristica.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /caracteristicas} : get all the caracteristicas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of caracteristicas in body.
     */
    @GetMapping("/caracteristicas")
    public List<Caracteristica> getAllCaracteristicas() {
        log.debug("REST request to get all Caracteristicas");
        return caracteristicaRepository.findAll();
    }

    /**
     * {@code GET  /caracteristicas/:id} : get the "id" caracteristica.
     *
     * @param id the id of the caracteristica to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the caracteristica, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/caracteristicas/{id}")
    public ResponseEntity<Caracteristica> getCaracteristica(@PathVariable Long id) {
        log.debug("REST request to get Caracteristica : {}", id);
        Optional<Caracteristica> caracteristica = caracteristicaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(caracteristica);
    }

    /**
     * {@code DELETE  /caracteristicas/:id} : delete the "id" caracteristica.
     *
     * @param id the id of the caracteristica to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/caracteristicas/{id}")
    public ResponseEntity<Void> deleteCaracteristica(@PathVariable Long id) {
        log.debug("REST request to delete Caracteristica : {}", id);
        caracteristicaRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
