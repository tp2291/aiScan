package com.cisco.wxcc.saa.abo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SwaggerResponseBody {
    @JsonProperty("active")
    public boolean active;

    @JsonProperty("capacity")
    public int capacity;

    @JsonProperty("createdTime")
    public long createdTime;

    @JsonProperty("description")
    public String description;

    @JsonProperty("desktopLayoutId")
    public String desktopLayoutId;

    @JsonProperty("dialedNumber")
    public String dialedNumber;

    @JsonProperty("id")
    public String id;

    @JsonProperty("lastUpdatedTime")
    public long lastUpdatedTime;

    @JsonProperty("multiMediaProfileId")
    public String multiMediaProfileId;

    @JsonProperty("name")
    public String name;

    @JsonProperty("organizationId")
    public String organizationId;

    @JsonProperty("siteId")
    public String siteId;

    @JsonProperty("siteName")
    public String siteName;

    @JsonProperty("skillProfileId")
    public String skillProfileId;

    @JsonProperty("systemDefault")
    public boolean systemDefault;

    @JsonProperty("teamStatus")
    public String teamStatus;

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    @JsonProperty("teamType")
    public String teamType;

    @JsonProperty("userIds")
    public String userIds;

    @JsonProperty("version")
    public int version;

    @Override
    public String toString() {
        return "Team{" +
                "active=" + active +
                ", capacity=" + capacity +
                ", createdTime=" + createdTime +
                ", description='" + description + '\'' +
                ", desktopLayoutId='" + desktopLayoutId + '\'' +
                ", dialedNumber='" + dialedNumber + '\'' +
                ", id='" + id + '\'' +
                ", lastUpdatedTime=" + lastUpdatedTime +
                ", multiMediaProfileId='" + multiMediaProfileId + '\'' +
                ", name='" + name + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", siteId='" + siteId + '\'' +
                ", siteName='" + siteName + '\'' +
                ", skillProfileId='" + skillProfileId + '\'' +
                ", systemDefault=" + systemDefault +
                ", teamStatus='" + teamStatus + '\'' +
                ", teamType='" + teamType + '\'' +
                ", userIds=" + userIds +
                ", version=" + version +
                '}';
    }






        }
