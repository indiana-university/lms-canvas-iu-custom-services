package edu.iu.uits.lms.iuonly.model.nodehierarchy;

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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by chmaurer on 2/22/16.
 */
@Entity
@Table(name = "NODE_HIERARCHY")
@SequenceGenerator(name = "NODE_HIERARCHY_ID_SEQ", sequenceName = "NODE_HIERARCHY_ID_SEQ", allocationSize = 1)
@Data
@Slf4j
public class NodeWrapper implements Serializable {

    @Id
    @GeneratedValue(generator = "NODE_HIERARCHY_ID_SEQ")
    private Long id;

    @Lob
    @Column(name = "hierarchy_bytes", columnDefinition = "TEXT")
    private byte[] hierarchy;

    @Transient
    private List<NodeCampus> campusList;

    private Date created;
    private Date modified;

// probably want this again after SisImportServiceImpl gets a microservice conversion
//    public List<NodeCampus> getCampusList() {
//        return (List<NodeCampus>) SerializationUtils.deserialize(hierarchy);
//    }

    public List<NodeCampus> getCampusList() {
        try {
            HackedObjectInputStream hois = new HackedObjectInputStream(new ByteArrayInputStream(hierarchy));
            return (List<NodeCampus>) hois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            log.error("uh oh", e);
        }
        return null;
    }

    public void setCampusList(List<NodeCampus> campusList) {
        this.campusList = campusList;
        this.hierarchy = SerializationUtils.serialize((Serializable) campusList);
    }

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        modified = new Date();
        if (created==null) {
            created = new Date();
        }
    }
}
