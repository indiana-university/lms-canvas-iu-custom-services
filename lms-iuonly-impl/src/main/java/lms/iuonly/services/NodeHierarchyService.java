package lms.iuonly.services;

import io.swagger.annotations.Api;
import lms.iuonly.model.nodehierarchy.NodeCampus;
import lms.iuonly.model.nodehierarchy.NodeSchool;
import lms.iuonly.model.nodehierarchy.NodeWrapper;
import lms.iuonly.repository.NodeHierarchyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tnguyen on 2/5/16.
 */

@RestController
@RequestMapping("/nodeHierarchy")
@Slf4j
@Api(tags = "nodeHierarchy")
public class NodeHierarchyService extends BaseService {

    @Autowired
    private NodeHierarchyRepository nodeHierarchyRepository;


    //TODO keep an eye on this one after SISImportServiceImpl conversion
    // likely don't need the Transactional on this method
//    @Transactional
    @PostMapping("/")
    @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
    public String writeHierarchy(@RequestBody List<NodeCampus> nodeHierarchys) {
        nodeHierarchyRepository.deleteAll();
        NodeWrapper nodeWrapper = new NodeWrapper();
        nodeWrapper.setCampusList(nodeHierarchys);
        nodeHierarchyRepository.save(nodeWrapper);

        return "Hierarchy created";
    }

    @GetMapping("/all")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public List<NodeCampus> readHierarchy() {
        List<NodeCampus> nodeHierarchys = null;
        NodeWrapper nodeWrapper = nodeHierarchyRepository.findTop1ByOrderByModifiedDesc();

        if (nodeWrapper != null) {
            nodeHierarchys = nodeWrapper.getCampusList();
        }

        return nodeHierarchys;
    }

    @GetMapping("/flattened")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public List<String> getFlattenedHierarchy() {
        List<String> flattened = new ArrayList<>();
        List<NodeCampus> hierarchy = readHierarchy();
        for (NodeCampus campus : hierarchy) {
            flattened.add(campus.getCampus());
            for (NodeSchool school : campus.getSchools()) {
                flattened.add(school.getSchool());
                if (school.getDepartments() != null) {
                    flattened.addAll(school.getDepartments());
                }
            }
        }

        return flattened;
    }
}
