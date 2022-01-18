package edu.iu.uits.lms.iuonly.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ListWrapper implements Serializable {
   private List<String> listItems;
}
