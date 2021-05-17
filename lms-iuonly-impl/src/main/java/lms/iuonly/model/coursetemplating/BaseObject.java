package lms.iuonly.model.coursetemplating;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseObject {

   @Column(name = "date_created")
   private OffsetDateTime dateCreated;

   @Column(name = "date_modified")
   private OffsetDateTime dateModified;

   @PreUpdate
   @PrePersist
   public void updateTimeStamps() {
      if (dateCreated==null) {
         dateCreated = OffsetDateTime.now();
      }
      dateModified = OffsetDateTime.now();
   }

}
