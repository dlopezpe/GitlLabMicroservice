package com.seido.micro.core.utils.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seido.micro.core.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Class to get GITHUB Manager
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitHubResource implements Serializable {

    @JsonProperty("owner")
    private String owner;
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
                ", owner=" + owner + '\'' +
                ", repositoryName=" + repositoryName + '\'' +
                ", repositoryName with format owner/repo=" + Utils.getFormatRepo(owner,repositoryName) + '\'' +
                ", baseBranchName=" + baseBranchName + '\'' +
                ", branchName=" + branchName + '\'' +
                ", tagName=" + tagName + '\'' +
                ", tagMessage=" + tagMessage + '\'' +
                ", commitSha='" + commitSha + '\'' +
                '}';
    }
}
