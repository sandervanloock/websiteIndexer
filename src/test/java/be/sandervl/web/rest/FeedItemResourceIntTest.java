package be.sandervl.web.rest;

import be.sandervl.WebsiteIndexerApp;
import be.sandervl.domain.FeedItem;
import be.sandervl.repository.FeedItemRepository;
import be.sandervl.service.FeedItemService;
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
 * Test class for the FeedItemResource REST controller.
 *
 * @see FeedItemResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebsiteIndexerApp.class)
public class FeedItemResourceIntTest {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    @Autowired
    private FeedItemRepository feedItemRepository;

    @Autowired
    private FeedItemService feedItemService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restFeedItemMockMvc;

    private FeedItem feedItem;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeedItem createEntity(EntityManager em) {
        FeedItem feedItem = new FeedItem()
            .text(DEFAULT_TEXT);
        return feedItem;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FeedItemResource feedItemResource = new FeedItemResource(feedItemService);
        this.restFeedItemMockMvc = MockMvcBuilders.standaloneSetup(feedItemResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        feedItem = createEntity(em);
    }

    @Test
    @Transactional
    public void createFeedItem() throws Exception {
        int databaseSizeBeforeCreate = feedItemRepository.findAll().size();

        // Create the FeedItem
        restFeedItemMockMvc.perform(post("/api/feed-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feedItem)))
            .andExpect(status().isCreated());

        // Validate the FeedItem in the database
        List<FeedItem> feedItemList = feedItemRepository.findAll();
        assertThat(feedItemList).hasSize(databaseSizeBeforeCreate + 1);
        FeedItem testFeedItem = feedItemList.get(feedItemList.size() - 1);
        assertThat(testFeedItem.getText()).isEqualTo(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    public void createFeedItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = feedItemRepository.findAll().size();

        // Create the FeedItem with an existing ID
        feedItem.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedItemMockMvc.perform(post("/api/feed-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feedItem)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<FeedItem> feedItemList = feedItemRepository.findAll();
        assertThat(feedItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = feedItemRepository.findAll().size();
        // set the field null
        feedItem.setText(null);

        // Create the FeedItem, which fails.

        restFeedItemMockMvc.perform(post("/api/feed-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feedItem)))
            .andExpect(status().isBadRequest());

        List<FeedItem> feedItemList = feedItemRepository.findAll();
        assertThat(feedItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFeedItems() throws Exception {
        // Initialize the database
        feedItemRepository.saveAndFlush(feedItem);

        // Get all the feedItemList
        restFeedItemMockMvc.perform(get("/api/feed-items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feedItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }

    @Test
    @Transactional
    public void getFeedItem() throws Exception {
        // Initialize the database
        feedItemRepository.saveAndFlush(feedItem);

        // Get the feedItem
        restFeedItemMockMvc.perform(get("/api/feed-items/{id}", feedItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(feedItem.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFeedItem() throws Exception {
        // Get the feedItem
        restFeedItemMockMvc.perform(get("/api/feed-items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFeedItem() throws Exception {
        // Initialize the database
        feedItemService.save(feedItem);

        int databaseSizeBeforeUpdate = feedItemRepository.findAll().size();

        // Update the feedItem
        FeedItem updatedFeedItem = feedItemRepository.findOne(feedItem.getId());
        updatedFeedItem
            .text(UPDATED_TEXT);

        restFeedItemMockMvc.perform(put("/api/feed-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFeedItem)))
            .andExpect(status().isOk());

        // Validate the FeedItem in the database
        List<FeedItem> feedItemList = feedItemRepository.findAll();
        assertThat(feedItemList).hasSize(databaseSizeBeforeUpdate);
        FeedItem testFeedItem = feedItemList.get(feedItemList.size() - 1);
        assertThat(testFeedItem.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    public void updateNonExistingFeedItem() throws Exception {
        int databaseSizeBeforeUpdate = feedItemRepository.findAll().size();

        // Create the FeedItem

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFeedItemMockMvc.perform(put("/api/feed-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feedItem)))
            .andExpect(status().isCreated());

        // Validate the FeedItem in the database
        List<FeedItem> feedItemList = feedItemRepository.findAll();
        assertThat(feedItemList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteFeedItem() throws Exception {
        // Initialize the database
        feedItemService.save(feedItem);

        int databaseSizeBeforeDelete = feedItemRepository.findAll().size();

        // Get the feedItem
        restFeedItemMockMvc.perform(delete("/api/feed-items/{id}", feedItem.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<FeedItem> feedItemList = feedItemRepository.findAll();
        assertThat(feedItemList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeedItem.class);
        FeedItem feedItem1 = new FeedItem();
        feedItem1.setId(1L);
        FeedItem feedItem2 = new FeedItem();
        feedItem2.setId(feedItem1.getId());
        assertThat(feedItem1).isEqualTo(feedItem2);
        feedItem2.setId(2L);
        assertThat(feedItem1).isNotEqualTo(feedItem2);
        feedItem1.setId(null);
        assertThat(feedItem1).isNotEqualTo(feedItem2);
    }
}
