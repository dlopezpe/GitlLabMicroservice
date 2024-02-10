package com.seido.micro.core.web.controller;


import com.seido.micro.core.service.GitHubService;
import com.seido.micro.core.utils.exception.ValidationException;
import com.seido.micro.core.utils.resource.GitHubResource;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.HttpURLConnection;

/**
 * Controler for the Job Rest API
 */
@RestController
@RequestMapping("/api/github")
public class GitHubControler {
    private static final Logger LOG = LogManager.getLogger(GitHubControler.class);
    private static final String PATH_REPOSITORY = "/{repository}";

    @Autowired
    protected ModelMapper mapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private GitHubService gitHubService;

    protected static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    /**
     * Create a new repository
     *
     * @param repository Repository name
     * @return ResponseEntity<>
     * @throws ValidationException ValidationException
     */
    @ApiOperation(value = "Create a repository", response = GitHubResource.class)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = HttpURLConnection.HTTP_CREATED,
                            message = "Returns a gitResource"),
                    @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad request"),
                    @ApiResponse(
                            code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                            message = "Internal Server Error")
            })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/createRepository")
    public ResponseEntity<GitHubResource> createRepository(
            @RequestParam
            @Valid
            String repository)
            throws ValidationException {

        LOG.info("INIT: Rest service createRepository");
        LOG.info("Validate fields");

        // Is true validating all fields and false alone validated environment and scope
        GitHubResource gitResource =new GitHubResource(repository,null,null,null,null,null );
        validateGitResource(gitResource, false);

        gitHubService.createRepository(repository);

        HttpHeaders headers = createHeaders();

        headers.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(PATH_REPOSITORY)
                        .buildAndExpand(gitResource.getRepositoryName())
                        .toUri());
        LOG.info("END: Rest service createRepository");

        return new ResponseEntity<>(gitResource, headers, HttpStatus.CREATED);
    }

    /**
     * Create a new branch
     *
     * @param repository name
     * @param branch name
     * @return ResponseEntity<>
     * @throws ValidationException ValidationException
     */
    @ApiOperation(value = "Create a branch", response = GitHubResource.class)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = HttpURLConnection.HTTP_CREATED,
                            message = "Returns a gitResource"),
                    @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad request"),
                    @ApiResponse(
                            code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                            message = "Internal Server Error")
            })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/createBranch")
    public ResponseEntity<GitHubResource> createBranch(
            @RequestParam
            @Valid
            String repository,
            @RequestParam
            @Valid
            String branch)
            throws ValidationException {

        LOG.info("INIT: Rest service createBranch");
        LOG.info("Validate fields");

        // Is true validating all fields and false alone validated environment and scope
        GitHubResource gitResource =new GitHubResource(repository,null,branch,null,null,null);
        validateGitResource(gitResource, true);

        GitHubResource gitHubResource= gitHubService.createBranch(repository,branch);

        HttpHeaders headers = createHeaders();

        headers.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(PATH_REPOSITORY)
                        .buildAndExpand(gitResource.getRepositoryName())
                        .toUri());
        LOG.info("END: Rest service createBranch");

        return new ResponseEntity<>(gitHubResource, headers, HttpStatus.CREATED);
    }

    /**
     * Create a new branch
     *
     * @param gitHubResource GitHubResource
     * @return ResponseEntity<>
     * @throws ValidationException ValidationException
     */
    @ApiOperation(value = "Create a tag", response = GitHubResource.class)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = HttpURLConnection.HTTP_CREATED,
                            message = "Returns a gitResource"),
                    @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad request"),
                    @ApiResponse(
                            code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                            message = "Internal Server Error")
            })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/createTag", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<GitHubResource> createTag(
            @RequestBody
            @Valid
            GitHubResource gitHubResource)
            throws ValidationException {

        LOG.info("INIT: Rest service createBranch");
        LOG.info("Validate fields");

        // Is true validating all fields and false alone validated environment and scope
        validateGitResource(gitHubResource, true);

        GitHubResource gitResource= gitHubService.createTag(gitHubResource.getRepositoryName(),gitHubResource.getBranchName(),
                gitHubResource.getTagName(),gitHubResource.getTagMessage());

        HttpHeaders headers = createHeaders();

        headers.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(PATH_REPOSITORY)
                        .buildAndExpand(gitResource.getRepositoryName())
                        .toUri());
        LOG.info("END: Rest service createBranch");

        return new ResponseEntity<>(gitHubResource, headers, HttpStatus.CREATED);
    }

    /**
     * Validate all fields or alone environment and scope depends if create or find to query
     *
     * @param nexec     Param execution resource
     * @param allFields if true is all validations fields or false alone ENV and SCOPE
     * @throws ValidationException
     */
    private void validateGitResource(GitHubResource nexec, boolean allFields)
            throws ValidationException {

        StringBuilder msg = new StringBuilder();
        // Verify RepositoryName not null of empty
        if (StringUtils.isEmpty(nexec.getRepositoryName())) {
            msg.append("RepositoryName cannot be empty or null\n");
        }

        if (allFields) {
            // Verify BaseBranchName, if get default
            if (StringUtils.isEmpty(nexec.getBaseBranchName())) {
                nexec.setBaseBranchName(gitHubService.getDefaultBranch(nexec.getRepositoryName()));
            }
            // Verify BranchName not null of empty
            if (StringUtils.isEmpty(nexec.getBranchName())) {
                msg.append("BranchName cannot be empty or null\n");
            }
            // Verify TagName not null of empty
            if (StringUtils.isEmpty(nexec.getTagName())) {
                msg.append("TagName cannot be empty or null\n");
            }
            // Verify TagMessage is optional
            if (StringUtils.isEmpty(nexec.getTagMessage())) {
                nexec.setTagMessage("");
            }
            // Verify CommitSha is optional, if empty to get last commitSHA
            if (StringUtils.isEmpty(nexec.getCommitSha())) {
                nexec.setCommitSha(gitHubService.getCommitSha(nexec.getRepositoryName(), nexec.getBranchName()));
            }
        }
    }
}
