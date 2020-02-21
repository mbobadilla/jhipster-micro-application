package com.cda.micro.web.rest;

import com.cda.micro.JhipsterMicroservicesApp;
import com.cda.micro.domain.Caracteristica;
import com.cda.micro.repository.CaracteristicaRepository;
import com.cda.micro.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.cda.micro.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CaracteristicaResource} REST controller.
 */
@SpringBootTest(classes = JhipsterMicroservicesApp.class)
public class CaracteristicaResourceIT {

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Double DEFAULT_VALOR = 1D;
    private static final Double UPDATED_VALOR = 2D;

    @Autowired
    private CaracteristicaRepository caracteristicaRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restCaracteristicaMockMvc;

    private Caracteristica caracteristica;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CaracteristicaResource caracteristicaResource = new CaracteristicaResource(caracteristicaRepository);
        this.restCaracteristicaMockMvc = MockMvcBuilders.standaloneSetup(caracteristicaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Caracteristica createEntity(EntityManager em) {
        Caracteristica caracteristica = new Caracteristica()
            .descripcion(DEFAULT_DESCRIPCION)
            .valor(DEFAULT_VALOR);
        return caracteristica;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Caracteristica createUpdatedEntity(EntityManager em) {
        Caracteristica caracteristica = new Caracteristica()
            .descripcion(UPDATED_DESCRIPCION)
            .valor(UPDATED_VALOR);
        return caracteristica;
    }

    @BeforeEach
    public void initTest() {
        caracteristica = createEntity(em);
    }

    @Test
    @Transactional
    public void createCaracteristica() throws Exception {
        int databaseSizeBeforeCreate = caracteristicaRepository.findAll().size();

        // Create the Caracteristica
        restCaracteristicaMockMvc.perform(post("/api/caracteristicas")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(caracteristica)))
            .andExpect(status().isCreated());

        // Validate the Caracteristica in the database
        List<Caracteristica> caracteristicaList = caracteristicaRepository.findAll();
        assertThat(caracteristicaList).hasSize(databaseSizeBeforeCreate + 1);
        Caracteristica testCaracteristica = caracteristicaList.get(caracteristicaList.size() - 1);
        assertThat(testCaracteristica.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testCaracteristica.getValor()).isEqualTo(DEFAULT_VALOR);
    }

    @Test
    @Transactional
    public void createCaracteristicaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = caracteristicaRepository.findAll().size();

        // Create the Caracteristica with an existing ID
        caracteristica.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCaracteristicaMockMvc.perform(post("/api/caracteristicas")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(caracteristica)))
            .andExpect(status().isBadRequest());

        // Validate the Caracteristica in the database
        List<Caracteristica> caracteristicaList = caracteristicaRepository.findAll();
        assertThat(caracteristicaList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDescripcionIsRequired() throws Exception {
        int databaseSizeBeforeTest = caracteristicaRepository.findAll().size();
        // set the field null
        caracteristica.setDescripcion(null);

        // Create the Caracteristica, which fails.

        restCaracteristicaMockMvc.perform(post("/api/caracteristicas")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(caracteristica)))
            .andExpect(status().isBadRequest());

        List<Caracteristica> caracteristicaList = caracteristicaRepository.findAll();
        assertThat(caracteristicaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValorIsRequired() throws Exception {
        int databaseSizeBeforeTest = caracteristicaRepository.findAll().size();
        // set the field null
        caracteristica.setValor(null);

        // Create the Caracteristica, which fails.

        restCaracteristicaMockMvc.perform(post("/api/caracteristicas")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(caracteristica)))
            .andExpect(status().isBadRequest());

        List<Caracteristica> caracteristicaList = caracteristicaRepository.findAll();
        assertThat(caracteristicaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCaracteristicas() throws Exception {
        // Initialize the database
        caracteristicaRepository.saveAndFlush(caracteristica);

        // Get all the caracteristicaList
        restCaracteristicaMockMvc.perform(get("/api/caracteristicas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(caracteristica.getId().intValue())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].valor").value(hasItem(DEFAULT_VALOR.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getCaracteristica() throws Exception {
        // Initialize the database
        caracteristicaRepository.saveAndFlush(caracteristica);

        // Get the caracteristica
        restCaracteristicaMockMvc.perform(get("/api/caracteristicas/{id}", caracteristica.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(caracteristica.getId().intValue()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.valor").value(DEFAULT_VALOR.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCaracteristica() throws Exception {
        // Get the caracteristica
        restCaracteristicaMockMvc.perform(get("/api/caracteristicas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCaracteristica() throws Exception {
        // Initialize the database
        caracteristicaRepository.saveAndFlush(caracteristica);

        int databaseSizeBeforeUpdate = caracteristicaRepository.findAll().size();

        // Update the caracteristica
        Caracteristica updatedCaracteristica = caracteristicaRepository.findById(caracteristica.getId()).get();
        // Disconnect from session so that the updates on updatedCaracteristica are not directly saved in db
        em.detach(updatedCaracteristica);
        updatedCaracteristica
            .descripcion(UPDATED_DESCRIPCION)
            .valor(UPDATED_VALOR);

        restCaracteristicaMockMvc.perform(put("/api/caracteristicas")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCaracteristica)))
            .andExpect(status().isOk());

        // Validate the Caracteristica in the database
        List<Caracteristica> caracteristicaList = caracteristicaRepository.findAll();
        assertThat(caracteristicaList).hasSize(databaseSizeBeforeUpdate);
        Caracteristica testCaracteristica = caracteristicaList.get(caracteristicaList.size() - 1);
        assertThat(testCaracteristica.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testCaracteristica.getValor()).isEqualTo(UPDATED_VALOR);
    }

    @Test
    @Transactional
    public void updateNonExistingCaracteristica() throws Exception {
        int databaseSizeBeforeUpdate = caracteristicaRepository.findAll().size();

        // Create the Caracteristica

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCaracteristicaMockMvc.perform(put("/api/caracteristicas")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(caracteristica)))
            .andExpect(status().isBadRequest());

        // Validate the Caracteristica in the database
        List<Caracteristica> caracteristicaList = caracteristicaRepository.findAll();
        assertThat(caracteristicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCaracteristica() throws Exception {
        // Initialize the database
        caracteristicaRepository.saveAndFlush(caracteristica);

        int databaseSizeBeforeDelete = caracteristicaRepository.findAll().size();

        // Delete the caracteristica
        restCaracteristicaMockMvc.perform(delete("/api/caracteristicas/{id}", caracteristica.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Caracteristica> caracteristicaList = caracteristicaRepository.findAll();
        assertThat(caracteristicaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
