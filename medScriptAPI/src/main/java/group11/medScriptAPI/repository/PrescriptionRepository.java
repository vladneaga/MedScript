package group11.medScriptAPI.repository;

import group11.medScriptAPI.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link Prescription} entities.
 * Provides CRUD operations for managing prescriptions.
 */
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}
