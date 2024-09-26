package group11.medScriptAPI.repository;

import group11.medScriptAPI.entity.Patient;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Patient} entities.
 * Inherits CRUD operations from {@link PersonRepository}.
 */
@Repository
public interface PatientRepository extends PersonRepository<Patient> {
}
