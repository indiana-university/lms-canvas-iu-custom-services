package lms.iuonly.model.errorcontact;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ERROR_CONTACT_JOB_PROFILE")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class ErrorContactJobProfile implements Serializable {
    @Id
    @Column(name = "JOB_CODE")
    @NonNull
    private String jobCode;

    @Column(name = "DESCRIPTION")
    @NonNull
    private String description;

    @Column(name = "DUPLICATE_MINUTES_THRESHOLD")
    private Integer duplicateMinutesThreshold;

    @Column(name = "DUPLICATE_MAX_COUNT")
    private Integer duplicateMaxCount;
}
