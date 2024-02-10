package com.seido.micro.core.service;

import com.seido.micro.core.utils.Utils;
import com.seido.micro.core.utils.exception.ValidationException;
import com.seido.micro.core.utils.resource.GitHubResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.kohsuke.github.GitHub.*;

@Service
public class GitHubService {

    @Autowired
    private ModelMapper mapper;
    @Value("${github.api.token}") // Token del GIT
    private String gitHubToken;
    private static final Logger LOGGER = LogManager.getLogger(GitHubService.class);

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Method to create a repository in GITHUB
     * @param repositoryName Send parameter
     * @return Return to execution resource
     * @throws ValidationException Exception for to validation process to create
     */
    public String createRepository(String repositoryName) throws ValidationException {
        try{
            GitHub github = connectUsingOAuth(gitHubToken);

            // Crear repositorio
            GHCreateRepositoryBuilder builder = github.createRepository(repositoryName);
            GHRepository repository = builder.create();

            return Utils.getFormatRepo(repository.getOwnerName(),repositoryName);

        }catch (IOException e) {
            LOGGER.error(e);
            throw new ValidationException("Not create repository",e);
        }
    }

    private boolean doesRepositoryExist(String repositoryName) {
        try {
            GitHub github = GitHub.connectUsingOAuth(gitHubToken);
            GHRepository repository = github.getRepository(repositoryName);
            return repository != null;
        } catch (IOException e) {
            LOGGER.error(e);
            return false;
        }
    }

    public GitHubResource createRepositoryBranchAndTag(String repositoryName, String branchName, String tagName) throws ValidationException {
        try {
            GitHub github = GitHub.connectUsingOAuth(gitHubToken);

            // Crear repositorio
            GHCreateRepositoryBuilder builder = github.createRepository(repositoryName);
            GHRepository repository = builder.create();

            String baseBranchName =getDefaultBranch(repositoryName);
            String commitSha = getCommitSha(repositoryName, baseBranchName);

            // Crear una nueva rama
            repository.createRef("refs/heads/" + branchName,commitSha);

            // Crear una nueva etiqueta (tag)
            repository.createRef("refs/tags/" + tagName,commitSha);

            GitHubResource git = new GitHubResource(repository.getOwnerName(),repositoryName,baseBranchName,branchName,tagName,
                    repository.getTagObject(commitSha).getMessage(),commitSha);

            return mapper.map(git, GitHubResource.class);

        }catch (IOException e) {
            LOGGER.error(e);
            throw new ValidationException("Not create branch in repository"+ repositoryName+" this motive: "+e.getMessage());
        }
    }



    public String getDefaultBranch(String repositoryName)throws ValidationException {
        if (!doesRepositoryExist(repositoryName))
            throw new ValidationException("Repository does not exist: " + repositoryName);

        try {
            GitHub github = GitHub.connectUsingOAuth(gitHubToken);
            GHRepository repository = github.getRepository(repositoryName);

            // Obtener la rama predeterminada
            return repository.getDefaultBranch();
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }
    public GHRef checkGitRepositoryNotEmpty(String repositoryName) throws ValidationException {
        try {
            GitHub github = GitHub.connectUsingOAuth(gitHubToken);
            GHRepository repository = github.getRepository(repositoryName);

            // Obtener la referencia de la rama base
            String baseBranchName = getDefaultBranch(repositoryName);
            GHRef baseBranchRef = repository.getRef("heads/" + baseBranchName);
            return baseBranchRef;
        }catch (IOException e) {
                LOGGER.error(e);
                throw new ValidationException("Git repository is empty",e);
            }
    }

    public GitHubResource createBranch(String repositoryName, String newBranchName) throws ValidationException {
        if (!doesRepositoryExist(repositoryName))
            throw new ValidationException("Repository does not exist: " + repositoryName);

        try {
            GitHub github = GitHub.connectUsingOAuth(gitHubToken);
            GHRepository repository = github.getRepository(repositoryName);

            // Obtener la referencia de la rama base
            String baseBranchName = getDefaultBranch(repositoryName);
            GHRef baseBranchRef = checkGitRepositoryNotEmpty(repositoryName);

            // Crear una nueva rama a partir de la rama base
            repository.createRef("refs/heads/" + newBranchName, baseBranchRef.getObject().getSha());

            GitHubResource git = new GitHubResource(repository.getOwnerName(),repositoryName,baseBranchName,newBranchName,null
            ,null,baseBranchRef.getObject().getSha());

            return mapper.map(git, GitHubResource.class);
        }catch (IOException e) {
            LOGGER.error(e);
            throw new ValidationException("Not create branch in repository: "+ newBranchName+" this motive: "+e.getMessage());
        }
    }
    public String getCommitSha(String repositoryName, String branchName) throws ValidationException {
        if (!doesRepositoryExist(repositoryName))
            throw new ValidationException("Repository does not exist: " + repositoryName);

        try {
            GitHub github = GitHub.connectUsingOAuth(gitHubToken);
            GHRepository repository = github.getRepository(repositoryName);

            // Verificar si la rama existe
            GHBranch branch = repository.getBranches().get(branchName);

            if (branch == null) {
                throw new ValidationException("Branch does not exist: " + branchName);
            }
            // Obtener el SHA del último commit en la rama especificada
            return branch.getSHA1();

        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }

    public GitHubResource createTag(String repositoryName, String branchName, String tagName, String tagMessage) throws ValidationException {
        if (!doesRepositoryExist(repositoryName))
            throw new ValidationException("Repository does not exist: " + repositoryName);

        try {
            GitHub github = GitHub.connectUsingOAuth(gitHubToken);
            GHRepository repository = github.getRepository(repositoryName);
            // Obtener la referencia de la rama base
            String baseBranchName =getDefaultBranch(repositoryName);

            // Obtener el objeto commit al que apunta el SHA
            /**
             *     tag - The tag's name.
             *     message - The tag message.
             *     object - The SHA of the git object this is tagging.
             *     type - The type of the object we're tagging: "commit", "tree" or "blob".
             */
            String commitSha=getCommitSha(repositoryName,branchName);

            GHTagObject tagObject = repository.createTag(tagName,tagMessage,commitSha,"commit");

            // Crear una referencia (tag) al objeto commit
            repository.createRef("refs/tags/" + tagName, tagObject.getSha());

            GitHubResource git = new GitHubResource(repository.getOwnerName(),repositoryName,baseBranchName,branchName,tagName
                    ,tagMessage,tagObject.getSha());

            return git;
        }catch (IOException e) {
            LOGGER.error(e);
            throw new ValidationException("Not create tag in repository"+ tagName+" this motive: "+e.getMessage());
        }
    }
}
