package be.sandervl.service;

import be.sandervl.domain.FeedItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing FeedItem.
 */
public interface FeedItemService {

    /**
     * Save a feedItem.
     *
     * @param feedItem the entity to save
     * @return the persisted entity
     */
    FeedItem save(FeedItem feedItem);

    /**
     * Get all the feedItems.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<FeedItem> findAll(Pageable pageable);

    /**
     * Get the "id" feedItem.
     *
     * @param id the id of the entity
     * @return the entity
     */
    FeedItem findOne(Long id);

    /**
     * Delete the "id" feedItem.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
