package com.cda.micro.web.rest;

import com.cda.micro.JhipsterMicroservicesApp;
import com.cda.micro.domain.Subcategoria;
import com.cda.micro.repository.SubcategoriaRepository;
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
 * Integration tests for the {@link SubcategoriaResource} REST controller.
 */
@SpringBootTest(classes = JhipsterMicroservicesApp.class)
public class SubcategoriaResourceIT {

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

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

    private MockMvc restSubcategoriaMockMvc;

    private Subcategoria subcategoria;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SubcategoriaResource subcategoriaResource = new SubcategoriaResource(subcategoriaRepository);
        this.restSubcategoriaMockMvc = MockMvcBuilders.standaloneSetup(subcategoriaResource)
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
    public static Subcategoria createEntity(EntityManager em) {
        Subcategoria subcategoria = new Subcategoria()
            .descripcion(DEFAULT_DESCRIPCION);
        return subcategoria;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subcategoria createUpdatedEntity(EntityManager em) {
        Subcategoria subcategoria = new Subcategoria()
            .descripcion(UPDATED_DESCRIPCION);
        return subcategoria;
    }

    @BeforeEach
    public void initTest() {
        subcategoria = createEntity(em);
    }

    @Test
    @Transactional
    public void createSubcategoria() throws Exception {
        int databaseSizeBeforeCreate = subcategoriaRepository.findAll().size();

        // Create the Subcategoria
        restSubcategoriaMockMvc.perform(post("/api/subcategorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(subcategoria)))
            .andExpect(status().isCreated());

        // Validate the Subcategoria in the database
        List<Subcategoria> subcategoriaList = subcategoriaRepository.findAll();
        assertThat(subcategoriaList).hasSize(databaseSizeBeforeCreate + 1);
        Subcategoria testSubcategoria = subcategoriaList.get(subcategoriaList.size() - 1);
        assertThat(testSubcategoria.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
    }

    @Test
    @Transactional
    public void createSubcategoriaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = subcategoriaRepository.findAll().size();

        // Create the Subcategoria with an existing ID
        subcategoria.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubcategoriaMockMvc.perform(post("/api/subcategorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(subcategoria)))
            .andExpect(status().isBadRequest());

        // Validate the Subcategoria in the database
        List<Subcategoria> subcategoriaList = subcategoriaRepository.findAll();
        assertThat(subcategoriaList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDescripcionIsRequired() throws Exception {
        int databaseSizeBeforeTest = subcategoriaRepository.findAll().size();
        // set the field null
        subcategoria.setDescripcion(null);

        // Create the Subcategoria, which fails.

        restSubcategoriaMockMvc.perform(post("/api/subcategorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(subcategoria)))
            .andExpect(status().isBadRequest());

        List<Subcategoria> subcategoriaList = subcategoriaRepository.findAll();
        assertThat(subcategoriaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSubcategorias() throws Exception {
        // Initialize the database
        subcategoriaRepository.saveAndFlush(subcategoria);

        // Get all the subcategoriaList
        restSubcategoriaMockMvc.perform(get("/api/subcategorias?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subcategoria.getId().intValue())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)));
    }
    
    @Test
    @Transactional
    public void getSubcategoria() throws Exception {
        // Initialize the database
        subcategoriaRepository.saveAndFlush(subcategoria);

        // Get the subcategoria
        restSubcategoriaMockMvc.perform(get("/api/subcategorias/{id}", subcategoria.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subcategoria.getId().intValue()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION));
    }

    @Test
    @Transactional
    public void getNonExistingSubcategoria() throws Exception {
        // Get the subcategoria
        restSubcategoriaMockMvc.perform(get("/api/subcategorias/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSubcategoria() throws Exception {
        // Initialize the database
        subcategoriaRepository.saveAndFlush(subcategoria);

        int databaseSizeBeforeUpdate = subcategoriaRepository.findAll().size();

        // Update the subcategoria
        Subcategoria updatedSubcategoria = subcategoriaRepository.findById(subcategoria.getId()).get();
        // Disconnect from session so that the updates on updatedSubcategoria are not directly saved in db
        em.detach(updatedSubcategoria);
        updatedSubcategoria
            .descripcion(UPDATED_DESCRIPCION);

        restSubcategoriaMockMvc.perform(put("/api/subcategorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedSubcategoria)))
            .andExpect(status().isOk());

        // Validate the Subcategoria in the database
        List<Subcategoria> subcategoriaList = subcategoriaRepository.findAll();
        assertThat(subcategoriaList).hasSize(databaseSizeBeforeUpdate);
        Subcategoria testSubcategoria = subcategoriaList.get(subcategoriaList.size() - 1);
        assertThat(testSubcategoria.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    public void updateNonExistingSubcategoria() throws Exception {
        int databaseSizeBeforeUpdate = subcategoriaRepository.findAll().size();

        // Create the Subcategoria

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubcategoriaMockMvc.perform(put("/api/subcategorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(subcategoria)))
            .andExpect(status().isBadRequest());

        // Validate the Subcategoria in the database
        List<Subcategoria> subcategoriaList = subcategoriaRepository.findAll();
        assertThat(subcategoriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSubcategoria() throws Exception {
        // Initialize the database
        subcategoriaRepository.saveAndFlush(subcategoria);

        int databaseSizeBeforeDelete = subcategoriaRepository.findAll().size();

        // Delete the subcategoria
        restSubcategoriaMockMvc.perform(delete("/api/subcategorias/{id}", subcategoria.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Subcategoria> subcategoriaList = subcategoriaRepository.findAll();
        assertThat(subcategoriaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
