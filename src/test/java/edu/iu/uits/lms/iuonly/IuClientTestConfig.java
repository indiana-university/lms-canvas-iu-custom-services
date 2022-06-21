package edu.iu.uits.lms.iuonly;

/*-
 * #%L
 * lms-canvas-iu-custom-services
 * %%
 * Copyright (C) 2015 - 2022 Indiana University
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Indiana University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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

@TestConfiguration
public class IuClientTestConfig {

   @MockBean
   @Qualifier("postgresdbEntityMgrFactory")
   public LocalContainerEntityManagerFactoryBean ltiEntityMgrFactory;

   @MockBean
   @Qualifier("postgresdbTransactionMgr")
   public PlatformTransactionManager ltiTransactionMgr;

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
