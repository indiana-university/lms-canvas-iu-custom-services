package edu.iu.uits.lms.iuonly.repository;

import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactEvent;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component("ErrorContactEventRepository")
public interface ErrorContactEventRepository extends PagingAndSortingRepository<ErrorContactEvent, Long> {
    int numberOfJobCodesNoOlderThanMinutes(@Param("jobCode") String jobCode, @Param("minutes") int minutes);
}
