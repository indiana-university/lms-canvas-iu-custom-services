package lms.iuonly.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "FEATURE_ACCESS")
@SequenceGenerator(name = "FEATURE_ACCESS_ID_SEQ", sequenceName = "FEATURE_ACCESS_ID_SEQ", allocationSize = 1)
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class FeatureAccess implements Serializable {

//    public static final String FEATURE_BOX_SHARING = "box.sharing";
//    public static final String FEATURE_MULTITERM_CROSSLISTING = "multiterm.crosslisting";

    @Id
    @Column(name = "FEATURE_ACCESS_ID")
    @GeneratedValue(generator = "FEATURE_ACCESS_ID_SEQ")
    private Long id;

    @NonNull
    @Column(name = "FEATURE_ID")
    private String featureId;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Column
    private Date created;

    @Column
    private Date modified;

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        modified = new Date();
        if (created == null) {
            created = new Date();
        }
    }


}
