package com.seido.micro.core.web.controller;


import com.seido.micro.core.service.GitHubService;
import com.seido.micro.core.utils.Utils;
import com.seido.micro.core.utils.exception.ValidationException;
import com.seido.micro.core.utils.resource.GitHubResource;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Value("${github.api.owner}") // Owner del GIT
    private String gitHubOwner;


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
                    @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Bad credentials"),
                    @ApiResponse(
                            code = HttpURLConnection.HTTP_INTERNAL_ERROR,
                            message = "Internal Server Error")
            })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/createRepository")
    public ResponseEntity<GitHubResource> createRepository(
            @RequestParam
            @Valid
            String owner,
            @RequestParam
            @Valid
            String repository)
            throws ValidationException {

        LOG.info("INIT: Rest service createRepository");
        LOG.info("Validate fields");

        // Is true validating all fields and false alone validated
        GitHubResource gitResource =new GitHubResource(owner,repository,null,null,null,null,null );
        validateGitResource(gitResource, false);

        String repo=gitHubService.createRepository(repository);

        HttpHeaders headers = createHeaders();

        headers.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(PATH_REPOSITORY)
                        .buildAndExpand(repo)
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
            String owner,
            @RequestParam
            @Valid
            String repository,
            @RequestParam
            @Valid
            String branch)
            throws ValidationException {

        LOG.info("INIT: Rest service createBranch");
        LOG.info("Validate fields");

        // Is true validating all fields and false alone validated
        GitHubResource gitResource =new GitHubResource(owner,repository,null,branch,null,null,null);
        validateGitResource(gitResource, true);
        String repo = Utils.getFormatRepo(owner,repository);

        GitHubResource gitHubResource= gitHubService.createBranch(repo,branch);

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

        // Is true validating all fields and false alone validated
        validateGitResource(gitHubResource, true);

        //Validate format tagname
        validateFormatTagNane(gitHubResource.getTagName());

        String repo = Utils.getFormatRepo(gitHubResource.getOwner(),gitHubResource.getRepositoryName());

        GitHubResource gitResource= gitHubService.createTag(repo,gitHubResource.getBranchName(),
                gitHubResource.getTagName(),gitHubResource.getTagMessage());

        HttpHeaders headers = createHeaders();

        headers.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(PATH_REPOSITORY)
                        .buildAndExpand(gitResource.getRepositoryName())
                        .toUri());
        LOG.info("END: Rest service createBranch");

        return new ResponseEntity<>(gitResource, headers, HttpStatus.CREATED);
    }

    /**
     * Para verificar el formato del tagname (nombre de etiqueta) en GitHub, debes tener en cuenta las siguientes reglas:
     *
     *     Longitud Máxima:
     *         La longitud máxima del tagname en GitHub es de 255 caracteres.
     *
     *     Caracteres Permitidos:
     *         Puedes utilizar letras (mayúsculas y minúsculas).
     *         Puedes utilizar números.
     *         Puedes utilizar guiones (-).
     *         Puedes utilizar puntos (.).
     *         Puedes utilizar barras inclinadas (/).
     *
     *     Restricciones Especiales:
     *         GitHub puede tener restricciones adicionales dependiendo del contexto del uso del tagname.
     *
     * Ejemplo de un tagname válido:
     *
     * plaintext
     *
     * v1.0.0
     * release-2.3
     * feature/awesome-feature
     *
     * Ejemplo de un tagname no válido:
     *
     * plaintext
     *
     * my tag // contiene un espacio
     * #invalid // contiene un carácter especial no permitido
     * too_long_tag_name_to_make_it_invalid // más de 255 caracteres
     * @param tagName
     */
    private void validateFormatTagNane(String tagName) {
        StringBuilder msg = new StringBuilder();
        if (tagName.length() >255){
            msg.append("La longitud máxima del tagname en GitHub es de 255 caracteres\n");
        }

        if (isValidTagName(tagName)){
            msg.append("Caracteres Permitidos:\n" +
                    "\n" +
                    "    Puedes utilizar letras (mayúsculas y minúsculas).\n" +
                    "    Puedes utilizar números.\n" +
                    "    Puedes utilizar guiones (-).\n" +
                    "    Puedes utilizar puntos (.).\n" +
                    "    Puedes utilizar barras inclinadas (/).\n");
        }
    }
    private static final String TAGNAME_REGEX = "^[a-zA-Z0-9_.\\-/]+$";
    private static final Pattern TAGNAME_PATTERN = Pattern.compile(TAGNAME_REGEX);

    public static boolean isValidTagName(String tagName) {
        Matcher matcher = TAGNAME_PATTERN.matcher(tagName);
        return matcher.matches();
    }


    /**
     * Validate all fields or alone depends if create or find to query
     *
     * @param nexec     Param execution resource
     * @param allFields if true is all validations fields or false alone ENV and SCOPE
     * @throws ValidationException
     */
    private void validateGitResource(GitHubResource nexec, boolean allFields)
            throws ValidationException {

        StringBuilder msg = new StringBuilder();

        // Verify Owner not null of empty, check information in app
        if (StringUtils.isEmpty(nexec.getOwner())) {
            if (StringUtils.isNoneEmpty(gitHubOwner)){
                nexec.setOwner(gitHubOwner);
            }else
                msg.append("Owner cannot be empty or null\n");
        }
        // Verify RepositoryName not null of empty
        if (StringUtils.isEmpty(nexec.getRepositoryName())) {
            msg.append("RepositoryName cannot be empty or null\n");
        }

        //Verify format owner and repository
        String[] tokens = nexec.getRepositoryName().split("/");
        if (tokens.length != 2)
            msg.append("Repository name must be in format owner/repo");

        if (allFields) {

            String repo=Utils.getFormatRepo(nexec.getOwner(),nexec.getRepositoryName());
            // Verify BaseBranchName, if get default
            if (StringUtils.isEmpty(nexec.getBaseBranchName())) {
                nexec.setBaseBranchName(gitHubService.getDefaultBranch(repo));
            }

            //Verify repository not empty
            gitHubService.checkGitRepositoryNotEmpty(repo);

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
            // Verify CommitSha is optional
        }
    }
}
