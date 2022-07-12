package edu.iu.uits.lms.iuonly.model;

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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Entity
@Table(name = "LMS_HIERARCHY_RESOURCE", uniqueConstraints = @UniqueConstraint(name = "node_u", columnNames = {"node"}))
@SequenceGenerator(name = "LMS_HIERARCHY_RESOURCE_ID_SEQ", sequenceName = "LMS_HIERARCHY_RESOURCE_ID_SEQ", allocationSize = 1)
@Data
@NoArgsConstructor
public class HierarchyResource {

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
