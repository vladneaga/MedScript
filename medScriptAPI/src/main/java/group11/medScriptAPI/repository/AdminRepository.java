package group11.medScriptAPI.repository;

import group11.medScriptAPI.entity.Admin;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Admin} entities.
 * Inherits common methods from {@link PersonRepository}.
 */
@Repository
public interface AdminRepository extends PersonRepository<Admin> {
}