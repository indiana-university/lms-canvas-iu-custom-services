package iuonly.helpers;

import iuonly.client.generated.model.NodeSchool;

import java.io.Serializable;
import java.util.Comparator;

public class NodeSchoolComparator implements Comparator<NodeSchool>, Serializable {

   @Override
   public int compare(NodeSchool o1, NodeSchool o2) {

      if (o1.getSchool() == null && o2.getSchool() == null) {
         return 0;
      }
      if (o1.getSchool() == null) {
         return 1;
      } else {
         if (o2.getSchool() == null) {
            return -1;
         }
      }

      return o1.getSchool().compareTo(o2.getSchool());
   }
}
