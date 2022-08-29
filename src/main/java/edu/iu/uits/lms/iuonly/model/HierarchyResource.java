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

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.iu.uits.lms.common.date.DateFormatUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "LMS_HIERARCHY_RESOURCE", uniqueConstraints = @UniqueConstraint(name = "node_u", columnNames = {"node"}))
@SequenceGenerator(name = "LMS_HIERARCHY_RESOURCE_ID_SEQ", sequenceName = "LMS_HIERARCHY_RESOURCE_ID_SEQ", allocationSize = 1)
@NamedQueries({
      @NamedQuery(name = "HierarchyResource.findByHomepageTemplate",
            query = "FROM HierarchyResource WHERE homepageTemplate = true")
})
@Data
@NoArgsConstructor
public class HierarchyResource implements Serializable {

    @Id
    @GeneratedValue(generator = "LMS_HIERARCHY_RESOURCE_ID_SEQ")
    private Long id;

    @Column(nullable = false)
    private String node;

    @Column(name = "contactusername")
    private String contactUsername;

    @Column(name = "contactemail")
    private String contactEmail;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_lms_file_storage_id"), name = "lms_file_storage_id", nullable = false)
    private StoredFile storedFile;

    @Column(name = "displayname")
    private String displayName;

    @Column(name = "contactname")
    private String contactName;

    @Column(name = "canvascommonsurl")
    private String canvasCommonsUrl;

    private String description;

    @Column(name = "defaulttemplate")
    private boolean defaultTemplate;

    @Column(name = "homepagetemplate")
    private boolean homepageTemplate;

    @Column(name = "source_course_id")
    private String sourceCourseId;

    private String sponsor;

    @JsonFormat(pattern= DateFormatUtil.JSON_DATE_FORMAT)
    @Column(name="createdon")
    private Date createdOn;
    @JsonFormat(pattern= DateFormatUtil.JSON_DATE_FORMAT)
    @Column(name="modifiedon")
    private Date modifiedOn;


    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        modifiedOn = new Date();
        if (createdOn==null) {
            createdOn = new Date();
        }
    }
}
