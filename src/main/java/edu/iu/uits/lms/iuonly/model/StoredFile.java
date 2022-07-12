package edu.iu.uits.lms.iuonly.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.iu.uits.lms.common.date.DateFormatUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "LMS_FILE_STORAGE")
@SequenceGenerator(name = "LMS_FILE_STORAGE_ID_SEQ", sequenceName = "LMS_FILE_STORAGE_ID_SEQ", allocationSize = 1)
@Data
@NoArgsConstructor
public class StoredFile {

   @Id
   @GeneratedValue(generator = "LMS_FILE_STORAGE_ID_SEQ")
   private Long id;

   @Column(name = "displayname")
   private String displayName;

   @Basic(fetch = FetchType.LAZY, optional = false)
   @JsonIgnore
   private byte[] content;

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
