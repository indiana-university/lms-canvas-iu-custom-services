package lms.iuonly.services;

import io.swagger.annotations.Api;
import lms.iuonly.model.DeptProvisioningUser;
import lms.iuonly.repository.DeptProvisioningUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deptProvisioningUser")
@Slf4j
@Api(tags = "deptProvisioningUser")
public class DeptProvisioningUserServiceImpl extends BaseService {

    @Autowired
    private DeptProvisioningUserRepository deptProvisioningUserRepository;

    @GetMapping("deptuser/username/{username}")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public DeptProvisioningUser findByUsername(@PathVariable("username") String username) {
        return deptProvisioningUserRepository.findByUsername(username);
    }

    @GetMapping("deptuser/canvasuserid/{canvasUserId}")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public DeptProvisioningUser findByCanvasUserId(@PathVariable("canvasUserId") String canvasUserId) {
        return deptProvisioningUserRepository.findByCanvasUserId(canvasUserId);
    }
}
