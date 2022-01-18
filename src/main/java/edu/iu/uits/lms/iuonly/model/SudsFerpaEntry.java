package edu.iu.uits.lms.iuonly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SudsFerpaEntry implements Serializable {
    private String ferpa;
    private String iuImsUsername;
}
