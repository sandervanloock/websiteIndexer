package be.sandervl.repository;

import be.sandervl.domain.Selector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the Selector entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SelectorRepository extends JpaRepository<Selector, Long> {

}
