package edu.iu.uits.lms.iuonly;

import edu.iu.uits.lms.iuonly.repository.DeptProvisioningUserRepository;
import edu.iu.uits.lms.iuonly.repository.ErrorContactEventRepository;
import edu.iu.uits.lms.iuonly.repository.ErrorContactJobProfileRepository;
import edu.iu.uits.lms.iuonly.repository.FeatureAccessRepository;
import edu.iu.uits.lms.iuonly.repository.LmsBatchEmailRepository;
import edu.iu.uits.lms.iuonly.repository.NodeHierarchyRepository;
import edu.iu.uits.lms.iuonly.repository.TemplatedCourseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@TestConfiguration
public class IuClientTestConfig {

   @MockBean
   @Qualifier("postgresdb")
   public DataSource dataSource;

   @MockBean
   @Qualifier("postgresdbEntityMgrFactory")
   public LocalContainerEntityManagerFactoryBean ltiEntityMgrFactory;

   @MockBean
   @Qualifier("postgresdbTransactionMgr")
   public PlatformTransactionManager ltiTransactionMgr;

   @MockBean
   @Qualifier("denododb")
   public DataSource denodoDataSource;

   /*
      Mock all the repositories
    */

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
}
