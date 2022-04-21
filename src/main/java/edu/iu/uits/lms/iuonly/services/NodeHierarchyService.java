package edu.iu.uits.lms.iuonly.services;

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
