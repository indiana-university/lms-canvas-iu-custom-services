package edu.iu.uits.lms.iuonly.repository;

import edu.iu.uits.lms.iuonly.model.DeptProvisioningUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface DeptProvisioningUserRepository extends PagingAndSortingRepository<DeptProvisioningUser, Long> {

   DeptProvisioningUser findByUsername(@Param("username") String username);
   DeptProvisioningUser findByCanvasUserId(@Param("canvasUserId") String canvasUserId);

}
