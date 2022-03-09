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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ERROR_CONTACT_JOB_PROFILE")
@NamedQueries({
        @NamedQuery(name = "ErrorContactJobProfile.activateAllJobProfiles",
                query = "UPDATE ErrorContactJobProfile SET active = true"),
        @NamedQuery(name = "ErrorContactJobProfile.deactivateAllJobProfiles",
                query = "UPDATE ErrorContactJobProfile SET active = false")
})
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class ErrorContactJobProfile implements Serializable {
    @Id
    @Column(name = "JOB_CODE")
    @NonNull
    private String jobCode;

    @JsonIgnore
    @OneToMany(mappedBy = "errorContactJobProfile", fetch = FetchType.LAZY)
    private List<ErrorContactEvent> errorContactEvents;

    @Column(name = "DESCRIPTION")
    @NonNull
    private String description;

    @Column(name = "ACTIVE")
    @NonNull
    private Boolean active;

    @Column(name = "DUPLICATE_MINUTES_THRESHOLD")
    private Integer duplicateMinutesThreshold;

    @Column(name = "DUPLICATE_MAX_COUNT")
    private Integer duplicateMaxCount;

    @PreUpdate
    @PrePersist
    public void updateActive() {
        if (active == null) {
            active = true;
        }
    }
}
