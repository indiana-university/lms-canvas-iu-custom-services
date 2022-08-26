package edu.iu.uits.lms.iuonly.services.rest;

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

import edu.iu.uits.lms.iuonly.model.HierarchyResource;
import edu.iu.uits.lms.iuonly.model.ToggledHomepageTemplates;
import edu.iu.uits.lms.iuonly.repository.HierarchyResourceRepository;
import edu.iu.uits.lms.iuonly.services.HierarchyResourceException;
import edu.iu.uits.lms.iuonly.services.HierarchyResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/rest/iu/hrm"})
@Tag(name = "HierarchyResourceManagerRestController", description = "Operations involving the HierarchyResource table")
public class HierarchyResourceManagerRestController {

    @Autowired
    private HierarchyResourceRepository hierarchyResourceRepository;

    @Autowired
    private HierarchyResourceService hierarchyResourceService;

    @GetMapping("/all")
    @Operation(summary = "Get all HierarchyResources (templates)")
    public List<HierarchyResource> getAllNodes() {
        List<HierarchyResource> hierarchyResources = (List<HierarchyResource>) hierarchyResourceRepository.findAll();
        return hierarchyResources;
    }

    @GetMapping("/resourceId/{id}")
    @Operation(summary = "Get a specific HierarchyResource (template) by id")
    public HierarchyResource getNodeFromId(@PathVariable Long id) {
        return hierarchyResourceRepository.findById(id).orElse(null);
    }

    @GetMapping("/homepagetemplate")
    @Operation(summary = "Get the HierarchyResource (template) marked as the 'homepage' template")
    public HierarchyResource getCurrentHomepageTemplate() throws HierarchyResourceException {
        return hierarchyResourceService.getHomePageTemplate();
    }

    @GetMapping("/{nodeName}")
    @Operation(summary = "Get all HierarchyResources (templates) in a given node")
    public List<HierarchyResource> getNodeFromNodeName(@PathVariable String nodeName) {
        return hierarchyResourceRepository.findByNode(nodeName);
    }

    @PostMapping("/{id}/homepagetemplate")
    @Operation(summary = "Set the HierarchyResource (template) with the given id as the new 'homepage' template and unset the old one, if previously set")
    public ToggledHomepageTemplates setNewHomepageTemplate(@PathVariable Long id) throws HierarchyResourceException {
        ToggledHomepageTemplates toggledHomepageTemplate = hierarchyResourceService.toggleHomepageTemplates(id);
        return toggledHomepageTemplate;
    }
}
