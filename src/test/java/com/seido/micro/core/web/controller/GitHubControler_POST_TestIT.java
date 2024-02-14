package com.seido.micro.core.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seido.micro.core.ManagementServiceApplication;
import com.seido.micro.core.utils.resource.GitHubResource;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ManagementServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ComponentScan({"com.seido.micro.core"})
public class GitHubControler_POST_TestIT {

    @Autowired
    private GitHubControler gitHubController;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contexLoads() {
        assertThat(gitHubController).isNotNull();
    }

    private final String jsonOutput= "{\"owner\":\"ownerValue\",\"repositoryName\":\"repositoryNameValue\",\"baseBranchName\":\"baseBranchNameValue\",\"branchName\":\"branchNameValue\",\"tagName\":\"tagNameValue\",\"tagMessage\":\"tagMessageValue\",\"commitSha\":\"commitShaValue\"}";


    @Test
    public void testCreateRepository() throws Exception {
        GitHubResource expectedGitHubResource = new GitHubResource("ownerValue","repositoryNameValue","baseBranchNameValue",
                "branchNameValue","tagNameValue","tagMessageValue","commitShaValue");

        mockMvc.perform(post("/api/github/createRepository").contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsBytes(expectedGitHubResource))
                )//NO CSRF
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.repositoryName", is("repositoryNameValue")));
    }

}