package be.sandervl.repository;

import be.sandervl.domain.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Attribute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    @Query("select distinct attribute from Attribute attribute left join fetch attribute.relatives")
    List<Attribute> findAllWithEagerRelationships();

    @Query("select attribute from Attribute attribute left join fetch attribute.relatives where attribute.id =:id")
    Attribute findOneWithEagerRelationships(@Param("id") Long id);

}
