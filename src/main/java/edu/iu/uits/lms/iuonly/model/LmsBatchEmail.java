package edu.iu.uits.lms.iuonly.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Created by yingwang on 10/29/15.
 */
@Entity
@Table(name = "LMS_BATCH_EMAIL")

@NamedQuery(name = "LmsBatchEmail.getBatchEmailFromGroupCode", query = "from LmsBatchEmail be where be.groupCode = :groupCode")

@SequenceGenerator(name = "LMS_BATCH_EMAIL_ID_SEQ", sequenceName = "LMS_BATCH_EMAIL_ID_SEQ", allocationSize = 1)
@Data
@NoArgsConstructor
public class LmsBatchEmail {
    @Id
    @GeneratedValue(generator = "LMS_BATCH_EMAIL_ID_SEQ")
    private Long batch_email_id;

    @Column(name = "GROUP_CODE")
    private String groupCode;

    @Column(name = "EMAILS", length = 4000)
    private String emails;

}
