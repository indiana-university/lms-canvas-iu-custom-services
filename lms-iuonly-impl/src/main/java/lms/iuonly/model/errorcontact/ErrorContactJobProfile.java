package lms.iuonly.model.errorcontact;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
