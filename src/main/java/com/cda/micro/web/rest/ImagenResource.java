package com.cda.micro.web.rest;

import com.cda.micro.domain.Imagen;
import com.cda.micro.repository.ImagenRepository;
import com.cda.micro.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.cda.micro.domain.Imagen}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ImagenResource {

    private final Logger log = LoggerFactory.getLogger(ImagenResource.class);

    private static final String ENTITY_NAME = "jhipsterMicroservicesImagen";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImagenRepository imagenRepository;

    public ImagenResource(ImagenRepository imagenRepository) {
        this.imagenRepository = imagenRepository;
    }

    /**
     * {@code POST  /imagens} : Create a new imagen.
     *
     * @param imagen the imagen to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new imagen, or with status {@code 400 (Bad Request)} if the imagen has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/imagens")
    public ResponseEntity<Imagen> createImagen(@RequestBody Imagen imagen) throws URISyntaxException {
        log.debug("REST request to save Imagen : {}", imagen);
        if (imagen.getId() != null) {
            throw new BadRequestAlertException("A new imagen cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Imagen result = imagenRepository.save(imagen);
        return ResponseEntity.created(new URI("/api/imagens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /imagens} : Updates an existing imagen.
     *
     * @param imagen the imagen to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated imagen,
     * or with status {@code 400 (Bad Request)} if the imagen is not valid,
     * or with status {@code 500 (Internal Server Error)} if the imagen couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/imagens")
    public ResponseEntity<Imagen> updateImagen(@RequestBody Imagen imagen) throws URISyntaxException {
        log.debug("REST request to update Imagen : {}", imagen);
        if (imagen.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Imagen result = imagenRepository.save(imagen);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, imagen.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /imagens} : get all the imagens.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of imagens in body.
     */
    @GetMapping("/imagens")
    public List<Imagen> getAllImagens() {
        log.debug("REST request to get all Imagens");
        return imagenRepository.findAll();
    }

    /**
     * {@code GET  /imagens/:id} : get the "id" imagen.
     *
     * @param id the id of the imagen to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the imagen, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/imagens/{id}")
    public ResponseEntity<Imagen> getImagen(@PathVariable Long id) {
        log.debug("REST request to get Imagen : {}", id);
        Optional<Imagen> imagen = imagenRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(imagen);
    }

    /**
     * {@code DELETE  /imagens/:id} : delete the "id" imagen.
     *
     * @param id the id of the imagen to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/imagens/{id}")
    public ResponseEntity<Void> deleteImagen(@PathVariable Long id) {
        log.debug("REST request to delete Imagen : {}", id);
        imagenRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
