package lms.iuonly.repository;

import lms.iuonly.model.errorcontact.ErrorContactJobProfile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component("ErrorContactJobProfileRepository")
public interface ErrorContactJobProfileRepository extends PagingAndSortingRepository<ErrorContactJobProfile, Long> {
    ErrorContactJobProfile findByJobCode(@Param("jobCode") String jobCode);
    void activateAllJobProfiles();
    void deactivateAllJobProfiles();
}
