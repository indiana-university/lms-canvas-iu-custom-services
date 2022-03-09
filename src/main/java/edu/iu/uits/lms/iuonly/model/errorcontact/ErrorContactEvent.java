package edu.iu.uits.lms.iuonly.model.errorcontact;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ERROR_CONTACT_EVENT")
@NamedQueries({
// sysdate - 30/blah blah equates to 30 minutes
        @NamedQuery(name = "ErrorContactEvent.numberOfJobCodesNoOlderThanMinutes",
                query = "select count(errorContactEvent) " +
                        "FROM ErrorContactEvent errorContactEvent " +
                        "WHERE errorContactJobProfile.jobCode = :jobCode AND " +
                               "errorContactJobProfile.active = true AND " +
                               "sysdate - :minutes / (24*60) <= errorContactEvent.created")
})
@SequenceGenerator(name = "ERROR_CONTACT_EVENT_ID_SEQ", sequenceName = "ERROR_CONTACT_EVENT_ID_SEQ", allocationSize = 1)
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class ErrorContactEvent implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "ERROR_CONTACT_EVENT_ID_SEQ")
    @NonNull
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "JOB_CODE", referencedColumnName = "JOB_CODE", nullable = false)
    private ErrorContactJobProfile errorContactJobProfile;

    @Column
    @NonNull
    private Date created;

    @PreUpdate
    @PrePersist
    public void updateTimeStamp() {
        if (created == null) {
            created = new Date();
        }
    }

    @Column
    @NonNull
    private String message;

    @Column
    @NonNull
    private String action;

    @Column(name = "PAGE_EVENT_ID")
    private String pageEventId;
}
