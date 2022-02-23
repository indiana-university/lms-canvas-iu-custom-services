package lms.iuonly.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.iu.uits.lms.common.date.DateFormatUtil;
import lombok.Data;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "DEPT_PROV_USERS")
@NamedQueries({
        @NamedQuery(name = "DeptProvisioningUser.findByCanvasUserId", query = "from DeptProvisioningUser where canvas_user_id = :canvasUserId"),
})
@SequenceGenerator(name = "DEPT_PROV_USERS_ID_SEQ", sequenceName = "DEPT_PROV_USERS_ID_SEQ", allocationSize = 1)
@Data
public class DeptProvisioningUser implements Serializable {

   @Id
   @GeneratedValue(generator = "DEPT_PROV_USERS_ID_SEQ")
   @Column(name = "DEPT_PROV_USERS_ID")
   private Long id;

   @Column(name = "DISPLAY_NAME")
   private String displayName;

   @Column(name = "USERNAME")
   private String username;

   @Column(name = "CANVAS_USER_ID")
   private String canvasUserId;

   @Column(name = "GROUP_CODE")
   @ElementCollection
   @CollectionTable(name = "DEPT_PROV_USER_GROUP", joinColumns = @JoinColumn(name = "DEPT_PROV_USERS_ID"))
   private List<String> groupCode;

   @Column(name = "ALLOW_SIS")
   private boolean allowSis;

   @Column(name = "AUTHORIZED_ACCOUNTS")
   private String authorizedAccounts;

   @Column(name = "OVERRIDE_RESTRICTIONS")
   private boolean overrideRestrictions;

   @Column(name = "CREATEDON")
   private Date createdOn;

   @Column(name = "MODIFIEDON")
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
