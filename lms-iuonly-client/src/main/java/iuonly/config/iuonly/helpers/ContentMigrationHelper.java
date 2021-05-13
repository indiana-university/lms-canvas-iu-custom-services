package iuonly.config.iuonly.helpers;

public class ContentMigrationHelper {

   public enum STATUS {
      PENDING,
      ERROR,
      COMPLETE
   }

   /**
    * Translate Canvas's status value into our internal representation
    * @param canvasMigrationStatus Canvas workflow_state
    * @return Internal status representation
    */
   public static STATUS translateStatus(String canvasMigrationStatus) {
      STATUS result = STATUS.PENDING;
      switch (canvasMigrationStatus) {
         case "completed":
            result = STATUS.COMPLETE;
            break;
         case "failed":
            result = STATUS.ERROR;
            break;
      }
      return result;
   }
}
