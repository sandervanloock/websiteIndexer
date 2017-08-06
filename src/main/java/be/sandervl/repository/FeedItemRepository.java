package be.sandervl.repository;

import be.sandervl.domain.FeedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the FeedItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeedItemRepository extends JpaRepository<FeedItem, Long> {

}
