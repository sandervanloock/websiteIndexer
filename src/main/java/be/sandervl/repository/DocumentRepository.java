package be.sandervl.repository;

import be.sandervl.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Document entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("select distinct document from Document document left join fetch document.matches")
    List<Document> findAllWithEagerRelationships();

    @Query("select document from Document document left join fetch document.matches where document.id =:id")
    Document findOneWithEagerRelationships(@Param("id") Long id);

    Optional<Document> findByUrl(String url);
}
