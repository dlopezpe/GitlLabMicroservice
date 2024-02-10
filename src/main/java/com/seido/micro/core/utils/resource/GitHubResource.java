package com.seido.micro.core.utils.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Class to get GITHUB Manager
 */
@Getter
@Setter
@AllArgsConstructor
public class GitHubResource implements Serializable {

    @JsonProperty("repositoryName")
    private String repositoryName;
    @JsonProperty("baseBranchName")
    private String baseBranchName;
    @JsonProperty("branchName")
    private String branchName;
    @JsonProperty("tagName")
    private String tagName;
    @JsonProperty("tagMessage")
    private String tagMessage;

    @JsonProperty("commitSha")
    private String commitSha;


    @Override
    public String toString() {
        return "gitResource{" +
                ", repositoryName=" + repositoryName + '\'' +
                ", baseBranchName=" + baseBranchName + '\'' +
                ", branchName=" + branchName + '\'' +
                ", tagName=" + tagName + '\'' +
                ", tagMessage=" + tagMessage + '\'' +
                ", commitSha='" + commitSha + '\'' +
                '}';
    }
}
