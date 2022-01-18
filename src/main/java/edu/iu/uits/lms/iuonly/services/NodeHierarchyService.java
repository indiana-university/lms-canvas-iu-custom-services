package edu.iu.uits.lms.iuonly.services;

import edu.iu.uits.lms.iuonly.model.nodehierarchy.NodeCampus;
import edu.iu.uits.lms.iuonly.model.nodehierarchy.NodeSchool;
import edu.iu.uits.lms.iuonly.model.nodehierarchy.NodeWrapper;
import edu.iu.uits.lms.iuonly.repository.NodeHierarchyRepository;
import edu.iu.uits.lms.iuonly.services.rest.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tnguyen on 2/5/16.
 */

@Service
@Slf4j
public class NodeHierarchyService extends BaseService {

    @Autowired
    private NodeHierarchyRepository nodeHierarchyRepository;


    //TODO keep an eye on this one after SISImportServiceImpl conversion
    // likely don't need the Transactional on this method
//    @Transactional
    public String writeHierarchy(List<NodeCampus> nodeHierarchys) {
        nodeHierarchyRepository.deleteAll();
        NodeWrapper nodeWrapper = new NodeWrapper();
        nodeWrapper.setCampusList(nodeHierarchys);
        nodeHierarchyRepository.save(nodeWrapper);

        return "Hierarchy created";
    }

    public List<NodeCampus> readHierarchy() {
        List<NodeCampus> nodeHierarchys = null;
        NodeWrapper nodeWrapper = nodeHierarchyRepository.findTop1ByOrderByModifiedDesc();

        if (nodeWrapper != null) {
            nodeHierarchys = nodeWrapper.getCampusList();
        }

        return nodeHierarchys;
    }

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
