package edu.iu.uits.lms.iuonly.services;

import edu.iu.uits.lms.iuonly.model.DeptProvisioningUser;
import edu.iu.uits.lms.iuonly.repository.DeptProvisioningUserRepository;
import edu.iu.uits.lms.iuonly.services.rest.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeptProvisioningUserServiceImpl extends BaseService {

    @Autowired
    private DeptProvisioningUserRepository deptProvisioningUserRepository;

    public DeptProvisioningUser findByUsername(String username) {
        return deptProvisioningUserRepository.findByUsername(username);
    }

    public DeptProvisioningUser findByCanvasUserId(String canvasUserId) {
        return deptProvisioningUserRepository.findByCanvasUserId(canvasUserId);
    }
}
