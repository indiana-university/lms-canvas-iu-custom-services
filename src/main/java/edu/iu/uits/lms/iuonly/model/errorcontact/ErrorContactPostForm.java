package edu.iu.uits.lms.iuonly.model.errorcontact;


import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorContactPostForm implements Serializable {
    private String jobCode;

    private String message;

    private boolean alwaysPage;
}
