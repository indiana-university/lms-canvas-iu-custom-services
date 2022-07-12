package edu.iu.uits.lms.iuonly.repository;

import edu.iu.uits.lms.iuonly.model.HierarchyResource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public interface HierarchyResourceRepository extends PagingAndSortingRepository<HierarchyResource, Long> {

    List<HierarchyResource> findByNode(String nodeName);
    List<HierarchyResource> findByNodeAndDefaultTemplateTrue(String nodeName);

    @Modifying
    @Transactional
    @Query("update HierarchyResource set defaulttemplate = :defaulttemplate where id = :templateId")
    int changeTemplateDefaultStatus(@Param("templateId") Long templateId, @Param("defaulttemplate") boolean defaulttemplate);
}
