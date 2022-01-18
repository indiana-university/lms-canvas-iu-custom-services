package edu.iu.uits.lms.iuonly.repository;

import edu.iu.uits.lms.iuonly.model.FeatureAccess;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("FeatureAccessRepository")
public interface FeatureAccessRepository extends PagingAndSortingRepository<FeatureAccess, Long> {
    List<FeatureAccess> findByFeatureId(@Param("featureId") String featureId);
}
