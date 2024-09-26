package group11.medScriptAPI.repository;

import group11.medScriptAPI.entity.Physician;

/**
 * Repository interface for {@link Physician} entities.
 * Inherits common CRUD operations from {@link PersonRepository}.
 */
public interface PhysicianRepository extends PersonRepository<Physician> {
}