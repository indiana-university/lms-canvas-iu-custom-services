package edu.iu.uits.lms.iuonly.model.coursetemplating;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CONTENT_MIGRATION_STATUSES")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"templatedCourse"})
@ToString(exclude = {"templatedCourse"})
public class ContentMigrationStatus extends BaseObject {

   @Id
   @Column(name = "CONTENT_MIGRATION_ID")
   @NonNull
   private String contentMigrationId;

   private int sequence;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "COURSE_ID", foreignKey = @ForeignKey(name = "fk_contmigstat_templcourses"))
   @JsonBackReference
   private TemplatedCourse templatedCourse;

   @NonNull
   protected String status;

}
