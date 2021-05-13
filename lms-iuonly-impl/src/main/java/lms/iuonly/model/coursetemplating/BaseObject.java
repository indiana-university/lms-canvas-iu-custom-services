package lms.iuonly.model.coursetemplating;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public class BaseObject {

   @Column(name = "date_created")
   private Date dateCreated;

   @Column(name = "date_modified")
   private Date dateModified;

   @PreUpdate
   @PrePersist
   public void updateTimeStamps() {
      if (dateCreated==null) {
         dateCreated = new Date();
      }
      dateModified = new Date();
   }

}
