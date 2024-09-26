package group11.medScriptAPI.repository;

import group11.medScriptAPI.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Institution} entities.
 * Provides basic CRUD operations through {@link JpaRepository}.
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

}
