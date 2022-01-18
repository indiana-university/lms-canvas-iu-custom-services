package edu.iu.uits.lms.iuonly.model.errorcontact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
@Data
public class ErrorContactResponse implements Serializable {
    @JsonProperty("ExternalID")
    private String externalId;

    @JsonProperty("StatusCode")
    private String statusCode;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("StatusDescription")
    private String statusDescription;

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("AppliedPolicies")
    private String appliedPolicies;

    @JsonProperty("Duplicates")
    private String duplicates;

    @JsonProperty("AlertsDelayed")
    private String alertsDelayed;

    @JsonProperty("AlertsCreated")
    private String alertsCreated;

    @JsonProperty("AlertIDs")
    private List<String> alertIds;
}






