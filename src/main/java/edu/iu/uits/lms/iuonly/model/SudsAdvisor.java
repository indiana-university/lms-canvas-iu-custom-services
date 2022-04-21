package edu.iu.uits.lms.iuonly.model;

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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SudsAdvisor implements Serializable {
    @Column(name = "EMPLID")
    private String emplId;

    @Column(name = "INSTITUTION")
    private String institution;

    @Column(name = "ADVISOR_ROLE")
    private String advisorRole;

    @Column(name = "STDNT_ADVISOR_NBR")
    private String studentAdvisorNumber;

    @Column(name = "ADVISOR_ID")
    private String advisorId;

    @Column(name = "ACAD_CAREER")
    private String academicCareer;

    @Column(name = "ACAD_PROG")
    private String academicProgram;

    @Column(name = "ACAD_PLAN")
    private String academicPlan;

    @Column(name = "DESCR")
    private String description;

    @Column(name = "ACAD_CAREER_DESCR")
    private String academicCareerDescription;

    @Column(name = "IU_IMS_USERNAME")
    private String advisorIuImsUsername;

    @Column(name = "EMAILID")
    private String advisorEmailId;

    @Column(name = "FIRST_NAME")
    private String advisorFirstName;

    @Column(name = "LAST_NAME")
    private String advisorLastName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IU_ACTIVE")
    private String iuActive;

    @Column(name = "AUDIT_STAMP")
    private String auditStampString;
}
