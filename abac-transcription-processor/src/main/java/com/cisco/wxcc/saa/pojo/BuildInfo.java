package com.cisco.wxcc.saa.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildInfo {

    private String appName;
    private String gitCommit;
    private String chartVersion;
    private String buildTag;
    private String buildUrl;
    private String buildId;
    private String buildNumber;
    private String gitBranch;


    public BuildInfo(String appName, String gitCommit, String chartVersion, String buildTag, //NOSONAR
                     String buildUrl, String buildId, String buildNumber, String gitBranch) { //NOSONAR

        this.appName = appName;
        this.gitCommit = gitCommit;
        this.chartVersion = chartVersion;
        this.buildTag = buildTag;
        this.buildUrl = buildUrl;
        this.buildId = buildId;
        this.buildNumber = buildNumber;
        this.gitBranch = gitBranch;
    }

    @JsonProperty("appName")
    public String appName() {
        return appName;
    }

    @JsonProperty("gitCommit")
    public String gitCommit() {
        return gitCommit;
    }

    @JsonProperty("chartVersion")
    public String gitChartVersion() {
        return chartVersion;
    }

    @JsonProperty("buildTag")
    public String buildTag() {
        return buildTag;
    }

    @JsonProperty("buildUrl")
    public String buildUrl() {
        return buildUrl;
    }

    @JsonProperty("buildId")
    public String buildId() {
        return buildId;
    }

    @JsonProperty("buildNumber")
    public String buildNumber() {
        return buildNumber;
    }

    @JsonProperty("gitBranch")
    public String gitBranch() {
        return gitBranch;
    }

}