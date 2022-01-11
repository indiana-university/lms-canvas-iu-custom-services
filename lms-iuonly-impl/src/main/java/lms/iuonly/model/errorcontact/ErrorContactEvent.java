package lms.iuonly.model.errorcontact;

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