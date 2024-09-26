package group11.medScriptAPI.repository;

import group11.medScriptAPI.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Insurance} entities.
 * Provides CRUD operations and custom query to find by number.
 */
@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
    /**
     * Finds a list of insurances by the given number.
     *
     * @param number the insurance number
     * @return a list of matching {@link Insurance} entities
     */
    List<Insurance> findByNumber(String number);
}