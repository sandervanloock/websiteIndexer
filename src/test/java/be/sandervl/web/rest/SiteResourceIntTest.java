package be.sandervl.web.rest;

import be.sandervl.WebsiteIndexerApp;
import be.sandervl.domain.Site;
import be.sandervl.repository.SiteRepository;
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
 * Test class for the SiteResource REST controller.
 *
 * @see SiteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebsiteIndexerApp.class)
public class SiteResourceIntTest {

	private static final String DEFAULT_NAME = "Resto.be";
	private static final String UPDATED_NAME = "Resto.fr";

	private static final String DEFAULT_REGEX = ".*";
	private static final String UPDATED_REGEX = "BBBBBBBBBB";

	private static final String DEFAULT_SEED = "http://www.resto.be";
	private static final String UPDATED_SEED = "http://nl.resto.be";

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSiteMockMvc;

    private Site site;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Site createEntity(EntityManager em) {
        Site site = new Site()
            .name(DEFAULT_NAME)
            .regex(DEFAULT_REGEX)
            .seed(DEFAULT_SEED);
        return site;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SiteResource siteResource = new SiteResource(siteRepository);
        this.restSiteMockMvc = MockMvcBuilders.standaloneSetup(siteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        site = createEntity(em);
    }

    @Test
    @Transactional
    public void createSite() throws Exception {
        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Create the Site
        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(site)))
            .andExpect(status().isCreated());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate + 1);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSite.getRegex()).isEqualTo(DEFAULT_REGEX);
        assertThat(testSite.getSeed()).isEqualTo(DEFAULT_SEED);
    }

    @Test
    @Transactional
    public void createSiteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Create the Site with an existing ID
        site.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(site)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setName(null);

        // Create the Site, which fails.

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(site)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRegexIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setRegex(null);

        // Create the Site, which fails.

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(site)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSites() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList
        restSiteMockMvc.perform(get("/api/sites?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(site.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].regex").value(hasItem(DEFAULT_REGEX.toString())))
            .andExpect(jsonPath("$.[*].seed").value(hasItem(DEFAULT_SEED.toString())));
    }

    @Test
    @Transactional
    public void getSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get the site
        restSiteMockMvc.perform(get("/api/sites/{id}", site.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(site.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.regex").value(DEFAULT_REGEX.toString()))
            .andExpect(jsonPath("$.seed").value(DEFAULT_SEED.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSite() throws Exception {
        // Get the site
        restSiteMockMvc.perform(get("/api/sites/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Update the site
        Site updatedSite = siteRepository.findOne(site.getId());
        updatedSite
            .name(UPDATED_NAME)
            .regex(UPDATED_REGEX)
            .seed(UPDATED_SEED);

        restSiteMockMvc.perform(put("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSite)))
            .andExpect(status().isOk());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSite.getRegex()).isEqualTo(UPDATED_REGEX);
        assertThat(testSite.getSeed()).isEqualTo(UPDATED_SEED);
    }

    @Test
    @Transactional
    public void updateNonExistingSite() throws Exception {
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Create the Site

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSiteMockMvc.perform(put("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(site)))
            .andExpect(status().isCreated());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeDelete = siteRepository.findAll().size();

        // Get the site
        restSiteMockMvc.perform(delete("/api/sites/{id}", site.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);
        Site site1 = new Site();
        site1.setId(1L);
        Site site2 = new Site();
        site2.setId(site1.getId());
        assertThat(site1).isEqualTo(site2);
        site2.setId(2L);
        assertThat(site1).isNotEqualTo(site2);
        site1.setId(null);
        assertThat(site1).isNotEqualTo(site2);
    }
}
