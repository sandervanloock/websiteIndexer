package be.sandervl.service.impl;

import be.sandervl.domain.FeedItem;
import be.sandervl.repository.FeedItemRepository;
import be.sandervl.service.FeedItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing FeedItem.
 */
@Service
@Transactional
public class FeedItemServiceImpl implements FeedItemService {

    private final Logger log = LoggerFactory.getLogger(FeedItemServiceImpl.class);

    private final FeedItemRepository feedItemRepository;

    public FeedItemServiceImpl(FeedItemRepository feedItemRepository) {
        this.feedItemRepository = feedItemRepository;
    }

    /**
     * Save a feedItem.
     *
     * @param feedItem the entity to save
     * @return the persisted entity
     */
    @Override
    public FeedItem save(FeedItem feedItem) {
        log.debug("Request to save FeedItem : {}", feedItem);
        return feedItemRepository.save(feedItem);
    }

    /**
     * Get all the feedItems.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FeedItem> findAll(Pageable pageable) {
        log.debug("Request to get all FeedItems");
        return feedItemRepository.findAll(pageable);
    }

    /**
     * Get one feedItem by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public FeedItem findOne(Long id) {
        log.debug("Request to get FeedItem : {}", id);
        return feedItemRepository.findOne(id);
    }

    /**
     * Delete the  feedItem by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete FeedItem : {}", id);
        feedItemRepository.delete(id);
    }
}
