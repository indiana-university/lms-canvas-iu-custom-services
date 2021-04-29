package lms.iuonly.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by yingwang on 2/4/16.
 */

@Data
public class CloseExpireCourse {
    private String canvasCourseId; //internal canvas course id, 12345
    private String courseName;
    private Date endDate;
    private String termId;
    private String emailAddress;
}
