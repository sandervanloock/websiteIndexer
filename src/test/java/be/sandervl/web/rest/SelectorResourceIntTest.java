package be.sandervl.web.rest;

import be.sandervl.WebsiteIndexerApp;
import be.sandervl.domain.Selector;
import be.sandervl.domain.Site;
import be.sandervl.repository.SelectorRepository;
import be.sandervl.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SelectorResource REST controller.
 *
 * @see SelectorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebsiteIndexerApp.class)
public class SelectorResourceIntTest {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ATTRIBUTE = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PRIMARY = false;
    private static final Boolean UPDATED_IS_PRIMARY = true;

    @Autowired
    private SelectorRepository selectorRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSelectorMockMvc;

    private Selector selector;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Selector createEntity(EntityManager em) {
        Selector selector = new Selector()
            .value(DEFAULT_VALUE)
            .name(DEFAULT_NAME)
            .attribute(DEFAULT_ATTRIBUTE)
            .isPrimary(DEFAULT_IS_PRIMARY);
        // Add required entity
        Site site = SiteResourceIntTest.createEntity(em);
        em.persist(site);
        em.flush();
        selector.setSite(site);
        return selector;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SelectorResource selectorResource = new SelectorResource(selectorRepository);
        this.restSelectorMockMvc = MockMvcBuilders.standaloneSetup(selectorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        selector = createEntity(em);
    }

    @Test
    @Transactional
    public void createSelector() throws Exception {
        int databaseSizeBeforeCreate = selectorRepository.findAll().size();

        // Create the Selector
        restSelectorMockMvc.perform(post("/api/selectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(selector)))
            .andExpect(status().isCreated());

        // Validate the Selector in the database
        List<Selector> selectorList = selectorRepository.findAll();
        assertThat(selectorList).hasSize(databaseSizeBeforeCreate + 1);
        Selector testSelector = selectorList.get(selectorList.size() - 1);
        assertThat(testSelector.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testSelector.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSelector.getAttribute()).isEqualTo(DEFAULT_ATTRIBUTE);
        assertThat(testSelector.isIsPrimary()).isEqualTo(DEFAULT_IS_PRIMARY);
    }

    @Test
    @Transactional
    public void createSelectorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = selectorRepository.findAll().size();

        // Create the Selector with an existing ID
        selector.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSelectorMockMvc.perform(post("/api/selectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(selector)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Selector> selectorList = selectorRepository.findAll();
        assertThat(selectorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = selectorRepository.findAll().size();
        // set the field null
        selector.setValue(null);

        // Create the Selector, which fails.

        restSelectorMockMvc.perform(post("/api/selectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(selector)))
            .andExpect(status().isBadRequest());

        List<Selector> selectorList = selectorRepository.findAll();
        assertThat(selectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = selectorRepository.findAll().size();
        // set the field null
        selector.setName(null);

        // Create the Selector, which fails.

        restSelectorMockMvc.perform(post("/api/selectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(selector)))
            .andExpect(status().isBadRequest());

        List<Selector> selectorList = selectorRepository.findAll();
        assertThat(selectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSelectors() throws Exception {
        // Initialize the database
        selectorRepository.saveAndFlush(selector);

        // Get all the selectorList
        restSelectorMockMvc.perform(get("/api/selectors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(selector.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].attribute").value(hasItem(DEFAULT_ATTRIBUTE.toString())))
            .andExpect(jsonPath("$.[*].isPrimary").value(hasItem(DEFAULT_IS_PRIMARY.booleanValue())));
    }

    @Test
    @Transactional
    public void getSelector() throws Exception {
        // Initialize the database
        selectorRepository.saveAndFlush(selector);

        // Get the selector
        restSelectorMockMvc.perform(get("/api/selectors/{id}", selector.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(selector.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.attribute").value(DEFAULT_ATTRIBUTE.toString()))
            .andExpect(jsonPath("$.isPrimary").value(DEFAULT_IS_PRIMARY.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSelector() throws Exception {
        // Get the selector
        restSelectorMockMvc.perform(get("/api/selectors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSelector() throws Exception {
        // Initialize the database
        selectorRepository.saveAndFlush(selector);
        int databaseSizeBeforeUpdate = selectorRepository.findAll().size();

        // Update the selector
        Selector updatedSelector = selectorRepository.findOne(selector.getId());
        updatedSelector
            .value(UPDATED_VALUE)
            .name(UPDATED_NAME)
            .attribute(UPDATED_ATTRIBUTE)
            .isPrimary(UPDATED_IS_PRIMARY);

        restSelectorMockMvc.perform(put("/api/selectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSelector)))
            .andExpect(status().isOk());

        // Validate the Selector in the database
        List<Selector> selectorList = selectorRepository.findAll();
        assertThat(selectorList).hasSize(databaseSizeBeforeUpdate);
        Selector testSelector = selectorList.get(selectorList.size() - 1);
        assertThat(testSelector.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testSelector.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSelector.getAttribute()).isEqualTo(UPDATED_ATTRIBUTE);
        assertThat(testSelector.isIsPrimary()).isEqualTo(UPDATED_IS_PRIMARY);
    }

    @Test
    @Transactional
    public void updateNonExistingSelector() throws Exception {
        int databaseSizeBeforeUpdate = selectorRepository.findAll().size();

        // Create the Selector

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSelectorMockMvc.perform(put("/api/selectors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(selector)))
            .andExpect(status().isCreated());

        // Validate the Selector in the database
        List<Selector> selectorList = selectorRepository.findAll();
        assertThat(selectorList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSelector() throws Exception {
        // Initialize the database
        selectorRepository.saveAndFlush(selector);
        int databaseSizeBeforeDelete = selectorRepository.findAll().size();

        // Get the selector
        restSelectorMockMvc.perform(delete("/api/selectors/{id}", selector.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Selector> selectorList = selectorRepository.findAll();
        assertThat(selectorList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Selector.class);
        Selector selector1 = new Selector();
        selector1.setId(1L);
        Selector selector2 = new Selector();
        selector2.setId(selector1.getId());
        assertThat(selector1).isEqualTo(selector2);
        selector2.setId(2L);
        assertThat(selector1).isNotEqualTo(selector2);
        selector1.setId(null);
        assertThat(selector1).isNotEqualTo(selector2);
    }
}
