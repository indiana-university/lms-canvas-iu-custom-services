package edu.iu.uits.lms.iuonly.model.coursetemplating;

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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TEMPLATE_AUDIT_INFO")
@SequenceGenerator(name = "TEMPLATE_AUDIT_INFO_ID_SEQ", sequenceName = "TEMPLATE_AUDIT_INFO_ID_SEQ", allocationSize = 1)
public class AuditInfo {

   /*
   Canvas Course ID
Course Code
Course Term Name
Course Term ID
Template ID
Template name (like 'IU Southeast ILTE Template')
Template file name
Template Account name (node)
Sponsor
Date template applied
New field to capture where is the data coming from
   Provisioning job
   Course setup wizard
   Apply template
   Reapply default template
New field to capture who applied the template - username or canvas3 if provisioning
    */

   @Id
   @GeneratedValue(generator = "TEMPLATE_AUDIT_INFO_ID_SEQ")
   private Long id;

   @Column(name = "CANVAS_COURSE_ID")
   private String canvasCourseId;

   @Column(name = "CANVAS_COURSE_CODE")
   private String canvasCourseCode;

   @Column(name = "CANVAS_TERM_NAME")
   private String canvasTermName;

   @Column(name = "CANVAS_TERM_ID")
   private String canvasTermId;

   @Column(name = "TEMPLATE_ID")
   private String templateId;

   @Column(name = "TEMPLATE_NAME")
   private String templateName;

   @Column(name = "TEMPLATE_FILE_NAME")
   private String templateFileName;

   @Column(name = "TEMPLATE_NODE")
   private String templateNode;

   @Column(name = "TEMPLATE_SPONSOR")
   private String templateSponsor;

   @Column(name = "ACTIVITY_DATE")
   private Date activityDate;

   @Column(name = "ACTIVITY_TYPE")
   private String activityType;

   @Column(name = "ACTIVITY_USER")
   private String activityUser;

   @PreUpdate
   @PrePersist
   public void updateTimeStamps() {
      if (activityDate == null) {
         activityDate = new Date();
      }
   }
}
