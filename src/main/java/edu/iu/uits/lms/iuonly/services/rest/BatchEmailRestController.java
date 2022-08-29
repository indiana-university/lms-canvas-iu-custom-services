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

import edu.iu.uits.lms.iuonly.model.LmsBatchEmail;
import edu.iu.uits.lms.iuonly.repository.LmsBatchEmailRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by yingwang on 10/29/15.
 */
@RestController
@RequestMapping("/rest/iu/batchemail")
@Tag(name = "BatchEmailRestController", description = "Operations involving the LmsBatchEmail table")
@Slf4j
public class BatchEmailRestController {

    @Autowired
    private LmsBatchEmailRepository lmsBatchEmailRepository;

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get LmsBatchEmail by id")
    public LmsBatchEmail getBatchEmailFromId(@PathVariable Long id) {
        return lmsBatchEmailRepository.findById(id).orElse(null);
    }

    @GetMapping(value = "/groupcode/{groupcode}")
    @Operation(summary = "Get LmsBatchEmail by groupcode")
    public LmsBatchEmail getBatchEmailFromGroupCode(@PathVariable String groupcode) {
        return lmsBatchEmailRepository.getBatchEmailFromGroupCode(groupcode);
    }

    @GetMapping(value = "/all")
    @Operation(summary = "Get all LmsBatchEmails")
    public List<LmsBatchEmail> getBatchEmailAll() {
        List<LmsBatchEmail> batchEmails = (List<LmsBatchEmail>) lmsBatchEmailRepository.findAll();
        return batchEmails;
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update an existing LmsBatchEmail by id with the provided comma separated string of emails")
    public LmsBatchEmail updateBatchEmail(@PathVariable Long id, @RequestBody String emails) {
        LmsBatchEmail updatedBatchEmail = lmsBatchEmailRepository.findById(id).orElse(null);
        updatedBatchEmail.setEmails(emails);
        return lmsBatchEmailRepository.save(updatedBatchEmail);
    }

    @PostMapping("/")
    @Operation(summary = "Create a new LmsBatchEmail")
    public LmsBatchEmail createBatchEmail(@RequestBody LmsBatchEmail lmsBatchEmail) {
        LmsBatchEmail newBatchEmail = new LmsBatchEmail();
        newBatchEmail.setGroupCode(lmsBatchEmail.getGroupCode());
        newBatchEmail.setEmails(lmsBatchEmail.getEmails());
        return lmsBatchEmailRepository.save(newBatchEmail);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete LmsBatchEmail by id")
    public String deleteBatchEmail(@PathVariable Long id) {
        lmsBatchEmailRepository.deleteById(id);
        return "Delete success.";
    }


}
