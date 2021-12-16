package edu.iu.uits.lms.iuonly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SudsAdvisor implements Serializable {
    @Column(name = "EMPLID")
    private String emplId;

    @Column(name = "INSTITUTION")
    private String institution;

    @Column(name = "ADVISOR_ROLE")
    private String advisorRole;

    @Column(name = "STDNT_ADVISOR_NBR")
    private String studentAdvisorNumber;

    @Column(name = "ADVISOR_ID")
    private String advisorId;

    @Column(name = "ACAD_CAREER")
    private String academicCareer;

    @Column(name = "ACAD_PROG")
    private String academicProgram;

    @Column(name = "ACAD_PLAN")
    private String academicPlan;

    @Column(name = "DESCR")
    private String description;

    @Column(name = "ACAD_CAREER_DESCR")
    private String academicCareerDescription;

    @Column(name = "IU_IMS_USERNAME")
    private String advisorIuImsUsername;

    @Column(name = "EMAILID")
    private String advisorEmailId;

    @Column(name = "FIRST_NAME")
    private String advisorFirstName;

    @Column(name = "LAST_NAME")
    private String advisorLastName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IU_ACTIVE")
    private String iuActive;

    @Column(name = "AUDIT_STAMP")
    private String auditStampString;
}
