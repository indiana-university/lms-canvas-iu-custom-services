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

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;


/**
 * Representation of the SIS data used to populate the TEMPLATED_COURSES table. These courses have been
 * processed by the CourseTemplating job
 */
@Entity
@Table(name = "TEMPLATED_COURSES",
      uniqueConstraints = @UniqueConstraint(name = "course_id_u", columnNames = {"course_id"}))
@Data
@NoArgsConstructor
public class TemplatedCourse extends BaseObject {

   @Id
   @Column(name = "course_id")
   @NonNull
   private String courseId;

   @Column(name = "sis_course_id")
   private String sisCourseId;

   @Column(name = "term_id")
   @NonNull
   private String termId;

   @NonNull
   private String status;

   @Column(name = "iu_crseld_status_added")
   @NonNull
   private boolean iu_crseld_status_added;

   public TemplatedCourse(String courseId, String sisCourseId, String termId, String status) {
      this.courseId = courseId;
      this.sisCourseId = sisCourseId;
      this.termId = termId;
      this.status = status;
   }

   @OneToMany(cascade = CascadeType.ALL, targetEntity = ContentMigrationStatus.class, mappedBy = "templatedCourse", fetch = FetchType.EAGER, orphanRemoval = true)
   @OrderColumn(name = "sequence")
   @JsonManagedReference
   private List<ContentMigrationStatus> contentMigrations = new ArrayList<>();


   public void addContentMigrations(ContentMigrationStatus contentMigrationStatus) {
      contentMigrationStatus.setTemplatedCourse(this);
      contentMigrations.add(contentMigrationStatus);
   }

}
