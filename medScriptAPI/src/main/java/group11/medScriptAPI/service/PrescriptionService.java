package group11.medScriptAPI.service;


import group11.medScriptAPI.entity.Prescription;
import group11.medScriptAPI.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

/**
 * Service class for managing prescriptions in the system.
 * This class provides methods for handling prescriptions, including pagination.
 */
@Service
public class PrescriptionService {
    final private PrescriptionRepository prescriptionRepository;


    /**
     * Constructor for PrescriptionService that initializes the prescription repository.
     *
     * @param prescriptionRepository the repository to manage prescription data
     */
    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Converts a list of prescriptions into a paginated format.
     *
     * @param pageable      the pagination information (page number and size)
     * @param prescriptions the list of prescriptions to paginate
     * @return a Page object containing the paginated prescriptions
     */
    public Page<Prescription> convertToPaginated(Pageable pageable, List<Prescription> prescriptions) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Prescription> list;
        if (prescriptions.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, prescriptions.size());
            list = prescriptions.subList(startItem, toIndex);
        }

        Page<Prescription> prescriptionPage
                //= prescriptionRepository.findAll(pageable);
                = new PageImpl<Prescription>(list, PageRequest.of(currentPage, pageSize), prescriptions.size());

        return prescriptionPage;
    }
}
