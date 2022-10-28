package edu.iu.uits.lms.iuonly.repository;

import edu.iu.uits.lms.iuonly.model.coursetemplating.AuditInfo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
public interface AuditInfoRepository extends PagingAndSortingRepository<AuditInfo, Long> {
}
