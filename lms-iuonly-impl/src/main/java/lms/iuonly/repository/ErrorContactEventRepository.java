package lms.iuonly.repository;

import lms.iuonly.model.errorcontact.ErrorContactEvent;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ErrorContactEventRepository")
public interface ErrorContactEventRepository extends PagingAndSortingRepository<ErrorContactEvent, Long> {
    List<ErrorContactEvent> findByJobCode(@Param("jobCode") String jobCode);

    int numberOfJobCodesNoOlderThanMinutes(@Param("jobCode") String jobCode, @Param("minutes") int minutes);
}
