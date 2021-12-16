package edu.iu.uits.lms.iuonly.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by yingwang on 12/4/15.
 */

@Data
public class Enrollment {
    private String canvasUserId;
    private String name;
    private String username;
    private String sisUserId;
    private String role;
    private String section;
    private String status;
    private Date createdDate;
    private Date updatedDate;
    private String primaryEmail;
}