package group11.medScriptAPI.repository;

import group11.medScriptAPI.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for entities extending {@link Person}.
 * Provides common CRUD operations for all Person-related entities.
 *
 * @param <T> The type of Person entity.
 */
@NoRepositoryBean
public interface PersonRepository<T extends Person> extends JpaRepository<T, Long> {

}
