package edu.iu.uits.lms.iuonly.rest;

import edu.iu.uits.lms.iuonly.repository.DeptProvisioningUserRepository;
import edu.iu.uits.lms.iuonly.repository.ErrorContactEventRepository;
import edu.iu.uits.lms.iuonly.repository.ErrorContactJobProfileRepository;
import edu.iu.uits.lms.iuonly.repository.FeatureAccessRepository;
import edu.iu.uits.lms.iuonly.repository.FileStorageRepository;
import edu.iu.uits.lms.iuonly.repository.HierarchyResourceRepository;
import edu.iu.uits.lms.iuonly.repository.LmsBatchEmailRepository;
import edu.iu.uits.lms.iuonly.repository.NodeHierarchyRepository;
import edu.iu.uits.lms.iuonly.repository.TemplatedCourseRepository;
import edu.iu.uits.lms.iuonly.services.FeatureAccessServiceImpl;
import edu.iu.uits.lms.iuonly.services.HierarchyResourceService;
import edu.iu.uits.lms.iuonly.services.rest.BatchEmailRestController;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan(basePackageClasses = BatchEmailRestController.class)
public class RestTestConfig {

   @MockBean
   public DeptProvisioningUserRepository deptProvisioningUserRepository;

   @MockBean
   public ErrorContactEventRepository errorContactEventRepository;

   @MockBean
   public ErrorContactJobProfileRepository errorContactJobProfileRepository;

   @MockBean
   public FeatureAccessRepository featureAccessRepository;

   @MockBean
   public LmsBatchEmailRepository lmsBatchEmailRepository;

   @MockBean
   public NodeHierarchyRepository nodeHierarchyRepository;

   @MockBean
   public TemplatedCourseRepository templatedCourseRepository;

   @MockBean
   public HierarchyResourceRepository hierarchyResourceRepository;

   @MockBean
   public FileStorageRepository fileStorageRepository;

   @MockBean
   private FeatureAccessServiceImpl featureAccessService;

   @MockBean
   private HierarchyResourceService hierarchyResourceService;

}
