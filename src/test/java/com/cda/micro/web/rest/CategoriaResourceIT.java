package com.cda.micro.web.rest;

import com.cda.micro.JhipsterMicroservicesApp;
import com.cda.micro.domain.Categoria;
import com.cda.micro.repository.CategoriaRepository;
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
 * Integration tests for the {@link CategoriaResource} REST controller.
 */
@SpringBootTest(classes = JhipsterMicroservicesApp.class)
public class CategoriaResourceIT {

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Autowired
    private CategoriaRepository categoriaRepository;

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

    private MockMvc restCategoriaMockMvc;

    private Categoria categoria;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CategoriaResource categoriaResource = new CategoriaResource(categoriaRepository);
        this.restCategoriaMockMvc = MockMvcBuilders.standaloneSetup(categoriaResource)
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
    public static Categoria createEntity(EntityManager em) {
        Categoria categoria = new Categoria()
            .descripcion(DEFAULT_DESCRIPCION);
        return categoria;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categoria createUpdatedEntity(EntityManager em) {
        Categoria categoria = new Categoria()
            .descripcion(UPDATED_DESCRIPCION);
        return categoria;
    }

    @BeforeEach
    public void initTest() {
        categoria = createEntity(em);
    }

    @Test
    @Transactional
    public void createCategoria() throws Exception {
        int databaseSizeBeforeCreate = categoriaRepository.findAll().size();

        // Create the Categoria
        restCategoriaMockMvc.perform(post("/api/categorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(categoria)))
            .andExpect(status().isCreated());

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll();
        assertThat(categoriaList).hasSize(databaseSizeBeforeCreate + 1);
        Categoria testCategoria = categoriaList.get(categoriaList.size() - 1);
        assertThat(testCategoria.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
    }

    @Test
    @Transactional
    public void createCategoriaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = categoriaRepository.findAll().size();

        // Create the Categoria with an existing ID
        categoria.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoriaMockMvc.perform(post("/api/categorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(categoria)))
            .andExpect(status().isBadRequest());

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll();
        assertThat(categoriaList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDescripcionIsRequired() throws Exception {
        int databaseSizeBeforeTest = categoriaRepository.findAll().size();
        // set the field null
        categoria.setDescripcion(null);

        // Create the Categoria, which fails.

        restCategoriaMockMvc.perform(post("/api/categorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(categoria)))
            .andExpect(status().isBadRequest());

        List<Categoria> categoriaList = categoriaRepository.findAll();
        assertThat(categoriaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCategorias() throws Exception {
        // Initialize the database
        categoriaRepository.saveAndFlush(categoria);

        // Get all the categoriaList
        restCategoriaMockMvc.perform(get("/api/categorias?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categoria.getId().intValue())))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)));
    }
    
    @Test
    @Transactional
    public void getCategoria() throws Exception {
        // Initialize the database
        categoriaRepository.saveAndFlush(categoria);

        // Get the categoria
        restCategoriaMockMvc.perform(get("/api/categorias/{id}", categoria.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(categoria.getId().intValue()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION));
    }

    @Test
    @Transactional
    public void getNonExistingCategoria() throws Exception {
        // Get the categoria
        restCategoriaMockMvc.perform(get("/api/categorias/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCategoria() throws Exception {
        // Initialize the database
        categoriaRepository.saveAndFlush(categoria);

        int databaseSizeBeforeUpdate = categoriaRepository.findAll().size();

        // Update the categoria
        Categoria updatedCategoria = categoriaRepository.findById(categoria.getId()).get();
        // Disconnect from session so that the updates on updatedCategoria are not directly saved in db
        em.detach(updatedCategoria);
        updatedCategoria
            .descripcion(UPDATED_DESCRIPCION);

        restCategoriaMockMvc.perform(put("/api/categorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCategoria)))
            .andExpect(status().isOk());

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
        Categoria testCategoria = categoriaList.get(categoriaList.size() - 1);
        assertThat(testCategoria.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    public void updateNonExistingCategoria() throws Exception {
        int databaseSizeBeforeUpdate = categoriaRepository.findAll().size();

        // Create the Categoria

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoriaMockMvc.perform(put("/api/categorias")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(categoria)))
            .andExpect(status().isBadRequest());

        // Validate the Categoria in the database
        List<Categoria> categoriaList = categoriaRepository.findAll();
        assertThat(categoriaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCategoria() throws Exception {
        // Initialize the database
        categoriaRepository.saveAndFlush(categoria);

        int databaseSizeBeforeDelete = categoriaRepository.findAll().size();

        // Delete the categoria
        restCategoriaMockMvc.perform(delete("/api/categorias/{id}", categoria.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Categoria> categoriaList = categoriaRepository.findAll();
        assertThat(categoriaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
