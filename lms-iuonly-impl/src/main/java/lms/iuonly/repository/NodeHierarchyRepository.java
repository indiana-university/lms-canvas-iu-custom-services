package lms.iuonly.repository;

import lms.iuonly.model.nodehierarchy.NodeWrapper;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by chmaurer on 2/22/16.
 */
@Component
public interface NodeHierarchyRepository extends PagingAndSortingRepository<NodeWrapper, Long> {

    @Transactional
    NodeWrapper findTop1ByOrderByModifiedDesc();
}
