package lms.iuonly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SudsCourse {
    private String classNumber;
    private String year;
    private String term;
    private String descriptionShort;
    private String campus;
    private String iuDeptCd;
    private String iuCourseCd;
    private String iuSiteId;
    private String description;
    private String iuCourseLoadStatus;
    private String iuScsFlag;
    private String status;
    private String iuActive;
    private String sTerm;
    private String instructionMode;
    private String etextIsbns;

}
