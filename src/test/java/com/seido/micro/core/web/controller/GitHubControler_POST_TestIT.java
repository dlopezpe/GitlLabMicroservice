package com.seido.micro.core.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "configrepo =ssh://git@gitlab.alm.gsnetcloud.corp:2220/ganesha-core-3/bigdata_wfm_api_properties.git")
@AutoConfigureMockMvc
@ActiveProfiles("test")
/* Para poder testear y que pase Jenkins ya que necesita la clave privada para conetarse
    al repositorio llamado configrepo en local y quitando el sufijo IT
 */
@TestPropertySource(
        locations = "classpath:/ssh-CORETRES-147/id_rsa")
@ComponentScan({"com.santander.supra.core"})
public class WfmExecutionsControler_POST_TestIT {

    //GOOD DATA
    private static final String APPLICATION_ENV = "APP_2";
    private static final String APPLICATION_SCOPE = "scope2";
    private static final String APPLICATION_SANDBOX = "sandbox2";
    private static final String APPLICATION_WORKFLOW = "hdfs-scenarioA";
    private static final String APPLICATION_WORKFLOW_1 = "hdfs-scenarioA_1";
    private static final String APPLICATION_LOAD_DATE = "20180101";
    private static final String APPLICATION_EXECUTION_DATE = "20180101000000";
    private static final String APPLICATION_TARGET = "TARGET";
    private static final String APPLICATION_FEED = "FEED";
    private static final Long APPLICATION_ID_100 = 100L;
    private static final String APPLICATION_ENV_100 = "PRUEBA_ENV";
    private static final String APPLICATION_SCOPE_100 = "PRUEBA_SCOPE";
    private static final String APPLICATION_SANDBOX_100 = "PRUEBA_SANDBOX";
    private static final String APPLICATION_WORKFLOW_100 = "PRUEBA_WFW";
    private static final String APPLICATION_LOAD_DATE_100 = "2018-02-08";
    private static final String APPLICATION_EXECUTION_DATE_100 = "2018-02-09121200";
    private static final int APPLICATION_EXECUTION_ID =13;
    private static final String APPLICATION_CONTAINER_ID= "application_1538666517146_0048";
    private static final String APPLICATION_STATUS_RELAUNCH="STOPPED";
    private static final Long APPLICATION_ID_101 = 101L;
    private static final int APPLICATION_ID_102 = 102;
    private static final String STOPPED = "STOPPED" ;
    private static final String STATUS_PENDING="PENDING";
    private static final String APPLICATION_ARGS = "{'default_config_file': '${VALIDATIONS_CONFIG}/default.config','output_config_file': '${VALIDATIONS_CONFIG_OUTPUT}/${tableName}.conf','class': 'com.santander.bigdata.core3.uv.engine.UnitValidationsDriver','jar': 'uv-engine-0.0.3-SNAPSHOT-shaded.jar','alias': 'UnitValidationConector','yarn_queue':'${yarn_queue}','name': 'Unit Validation Conector','master': 'yarn-cluster','deployMode': 'cluster','arguments': ['-c','hdfs:///desa3/flowlanding_ctrl/uv/conf/sprint003-bench/uvconfig.json']}";


    @Autowired
    WfmExecutionsControler wfmExecutionsControler;


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;



    @MockBean
    UserService userService;

    private String pvUser = "artudf";
    private String pvkeyBase64 = "LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFcFFJQkFBS0NBUUVBdm8rNmp4ZHNJMFg0bHRCTDkvajJobCtKeXN1Vm9pRFBBQWpDYWIxQ0xISWtUb3NWCmhJMUcvMWZFTHB1MnF0SURwN28xZk9tcEVhNmhuMHp6Zi9FSTlBQlpUQlZYdHRJSlladDFaaVh6amV1eUVQR3IKUHE0RzlBaUFXLy9HYmd5MlRldDAvemp2bDdNRkIzR3AvSk8ycHA5U3VBajR0VktiWVU5M3JyWDJKTCtlL3ZoMQpucG9NNDZFb0wxKy85emRhQnlKcW1VOWdpZE9pYS93cGRsYUYyVDhQdlNNUjJpYWQyRE9ucmUzQUEyd1BFSzhPCm5LcndsU2RmaDBPRk0zTC8vT0psVjFKYk5CZ0N2VHFRMUpNSWpRWTlsdG15R201Vm9oQXZNT01mM2pOd1RSRXoKS2NGMGtZWVhCMldQbTRzamZzQlRjeVM3Z251Ym1yenY2ZmpaYXdJREFRQUJBb0lCQVFDakl0WjZjM25QQkxMWgpJWDgzelhuMjlVazRSR3RZUGJBWWZtYzVNSjI3QVFoVitUbTFuV0MvYmpwMEVEZ2tLdUNZTVpCaTNJSXhzZ2VKCkljNS9wRENlK3NGM29mblNlQ014M1cxMndseXNEYkFHVTIzZm5nb3oxaWRWR3pXYnR1Ukc5cURWY2IralpjMlQKWHRwQWNaQjNPb1FOS3hrSDJhMU5kRU9vWFB4WTNmdDV4bFcrNW9SWnJzNDkrdVJST212U0dxd0NCMVFSeFhsdgpqWU96UWdtOHdoQ1JHcDcweWpnTkhqMldpOVlTakc1SEZGRGFLcHVsR0hIKzBlSkYxelRJTXRrSStjbkFuWmxOCkM5QTNUbTMzL0hQYmM5S0k4aVEvK2VMc2p3WDJORVVKQ05TQ1BzSzNuWXpGUUptQ2s4YmQ3Ti93K3hBaUw5dUEKREhOQnZ3OHhBb0dCQU84YmlranJXbGphVUdxTmpXemFLalEyb3pmSERvenA5U2FwSG0zc2xoM2JZQ1c4dW1nWApOQytpa3Q2YlF0TUxKeCtMV0MvMDc4Nkg4ZDFZbk1vN2hIOXZsRDYrQ2NmRTljQWhMV3VCeFJoMGlBUGNmNDZ5CkQ1TnpCK2pXNXU5YlJ2eUFkUjVoa1pLdVJ1OFJ3Y0c5ck5iUXpLM0cydHA3UG10N0hPaVZ4UGhqQW9HQkFNd0cKTVBLV0cvQTE3UWRUKzZCZDc4V082R3I1TDhzQ1kxMzNLNnQ3ZlQ3TWl0MU5adU5OVWVRZktMMzluTlpXRU9OQwpFaHkwR1UxdGVMYjBpTmFxbCtRMENmWk9YZEpuakhtaWg3VTdPczFkb2VLVDFwNDhtc09VVlFFcG9mRlBYZDdlCnlNTk9sZEphbkRrblFUNGN6OWVpVUNNZ0UzZGJCY1RNOWJNaVJqVlpBb0dCQU5vRjlLdlUzb2JTV2kwdWNWeGwKK2NHdWFGbDl5a2kzTS9CU21RcUdoY01udTUzdFh0TjNCUURDYnNWSU1VQ2FsTXZ3bFRMMVZveXQ3TjUxNUZHSwp2NkFycjdNZU9YRE1xWUROUDhzZGd3aHZpVzVyNm12RTlPcndJckF1OUZnZ2xMTzh0ZzVEelNkQWZqR1RzV2JsCmV1cE5iYlN1OC9Qd0dOU01aNm0wbHV3ZEFvR0JBSVFJamF5d0VDeUgrWGtBbEpyWHI4KytDQk1TYUNlTXlCbUwKVjZ1R0l5dDgyM0VVSWoxL1VEdTlIblMxcDhYZHROZ2RiZk05YWhKOWdkMkVxdm1oTk9MdCtuUjNpcWdiY3dGVQplVmFEbWk1RkpmcW5pZ0NsWDlLcUw4aXMxS3lCbkZJYkF2bkI1WnRZSURtc2VjQmtqTzBUa2FEcHBTOWNySU1MCjBRVzRiSThoQW9HQUVGaUNubE0xd0FCK3B0ZjVWMHRrbldEUUtQeWIvaTV0T2ZJNTcxYmd1MW1JSWwxYjNhb2kKd2ZrSXNYUjlYTWJ6YUFRMytidDYwN3czVmFZOU14OWxMUkNnMElZOUZVeGd3OExjeVhVMGpHaHc4YTVLWHFLdQpBRnJ0N0d3Zi9pTndwR081K1BYNnBJa0lYc3U5Z2dNbTBJL216YVVwVGE3cGE4YXhEdFVKOCtzPQotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo=";


    @MockBean
    TokenService tokenService;


    @Before
    public void setup() {

        String resourcePath = "src/test/resources/wmHome";

        Mockito.when(tokenService.getUsernameFromToken(Mockito.anyString())).thenReturn("DUMMY");

        Mockito.when(userService.validateUserApplication(Mockito.anyString(), Mockito.any(WfmExecutionResource.class)))
                .thenReturn(true);

    }

    @Test
    public void contexLoads() {
        assertThat(wfmExecutionsControler).isNotNull();
    }


    /**
     * Test positive case with Environment/Scope/Sandbox
     *
     * @throws Exception
     */
    @Test
    public void createWfmExecution_OK() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setEnvironment(APPLICATION_ENV);
        wfmExecutionResource.setScope(APPLICATION_SCOPE);
        wfmExecutionResource.setSandbox(APPLICATION_SANDBOX);
        wfmExecutionResource.setWorkflow(APPLICATION_WORKFLOW);
        wfmExecutionResource.setLoadDate(APPLICATION_LOAD_DATE);
        wfmExecutionResource.setExecutionDate(APPLICATION_EXECUTION_DATE);
        wfmExecutionResource.setTarget(APPLICATION_TARGET);
        wfmExecutionResource.setExecutionId(APPLICATION_EXECUTION_ID);
        wfmExecutionResource.setContainerId(APPLICATION_CONTAINER_ID);
        

        mockMvc.perform(post("/wfm/job").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource))
                .header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.environment", is(APPLICATION_ENV)))
                .andExpect(jsonPath("$.scope", is(APPLICATION_SCOPE)))
                .andExpect(jsonPath("$.sandbox", is(APPLICATION_SANDBOX)))
                .andExpect(jsonPath("$.workflow", is(APPLICATION_WORKFLOW)))
                .andExpect(jsonPath("$.load_date", is(APPLICATION_LOAD_DATE)))
                .andExpect(jsonPath("$.execution_date", is(APPLICATION_EXECUTION_DATE)));

    }

    private void simpleDelay(long t){
        long timeout = System.currentTimeMillis() + t;
        while(System.currentTimeMillis() < timeout);
    }


    /**
     * Test positive case with Environment/Scope whitout sandbox
     *
     * @throws Exception
     */
    @Test
    public void createWfmExecution_OK_1() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setEnvironment(APPLICATION_ENV);
        wfmExecutionResource.setScope(APPLICATION_SCOPE);
        wfmExecutionResource.setFeed(APPLICATION_FEED);
        wfmExecutionResource.setWorkflow(APPLICATION_WORKFLOW_1);
        wfmExecutionResource.setLoadDate(APPLICATION_LOAD_DATE);
        wfmExecutionResource.setExecutionDate(APPLICATION_EXECUTION_DATE);
        

        mockMvc.perform(post("/wfm/job").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
        )//NO CSRF
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.environment", is(APPLICATION_ENV)))
                .andExpect(jsonPath("$.scope", is(APPLICATION_SCOPE)))
                .andExpect(jsonPath("$.workflow", is(APPLICATION_WORKFLOW_1)))
                .andExpect(jsonPath("$.load_date", is(APPLICATION_LOAD_DATE)))
                .andExpect(jsonPath("$.execution_date", is(APPLICATION_EXECUTION_DATE)));
    }

    /**
     * Test positive case with args
     *
     * @throws Exception
     */
    @Test
    public void createWfmExecution_OK_with_Args() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setEnvironment(APPLICATION_ENV);
        wfmExecutionResource.setScope(APPLICATION_SCOPE);
        wfmExecutionResource.setFeed(APPLICATION_FEED);
        wfmExecutionResource.setWorkflow(APPLICATION_WORKFLOW_1);
        wfmExecutionResource.setLoadDate(APPLICATION_LOAD_DATE);
        wfmExecutionResource.setExecutionDate(APPLICATION_EXECUTION_DATE);
        wfmExecutionResource.setArgs(APPLICATION_ARGS);


        mockMvc.perform(post("/wfm/job").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
        )//NO CSRF
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.environment", is(APPLICATION_ENV)))
                .andExpect(jsonPath("$.scope", is(APPLICATION_SCOPE)))
                .andExpect(jsonPath("$.workflow", is(APPLICATION_WORKFLOW_1)))
                .andExpect(jsonPath("$.load_date", is(APPLICATION_LOAD_DATE)))
                .andExpect(jsonPath("$.execution_date", is(APPLICATION_EXECUTION_DATE)))
                .andExpect(jsonPath("$.args", is(APPLICATION_ARGS)));
    }

    /**
     * Test positive case with args
     *
     * @throws Exception
     */
    @Test
    public void createWfmExecution_OK_without_Args() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setEnvironment(APPLICATION_ENV);
        wfmExecutionResource.setScope(APPLICATION_SCOPE);
        wfmExecutionResource.setFeed(APPLICATION_FEED);
        wfmExecutionResource.setWorkflow(APPLICATION_WORKFLOW_1);
        wfmExecutionResource.setLoadDate(APPLICATION_LOAD_DATE);
        wfmExecutionResource.setExecutionDate(APPLICATION_EXECUTION_DATE);
        wfmExecutionResource.setArgs(null);


        mockMvc.perform(post("/wfm/job").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
        )//NO CSRF
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.environment", is(APPLICATION_ENV)))
                .andExpect(jsonPath("$.scope", is(APPLICATION_SCOPE)))
                .andExpect(jsonPath("$.workflow", is(APPLICATION_WORKFLOW_1)))
                .andExpect(jsonPath("$.load_date", is(APPLICATION_LOAD_DATE)))
                .andExpect(jsonPath("$.execution_date", is(APPLICATION_EXECUTION_DATE)))
                .andExpect(jsonPath("$.args", is("null")));
    }

    @Test
    public void resumeWfmExecution_OK() throws Exception {


        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setId(APPLICATION_ID_100);
        wfmExecutionResource.setExecutionId(APPLICATION_ID_101.intValue());


        mockMvc.perform(post("/wfm/job/resume").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.environment", is(APPLICATION_ENV_100)))
                .andExpect(jsonPath("$.id", not(APPLICATION_ID_100)))
                .andExpect(jsonPath("$.scope", is(APPLICATION_SCOPE_100)))
                .andExpect(jsonPath("$.workflow", is(APPLICATION_WORKFLOW_100)))
                .andExpect(jsonPath("$.load_date", is(APPLICATION_LOAD_DATE_100)))
                .andExpect(jsonPath("$.execution_date", is(APPLICATION_EXECUTION_DATE_100)))
                .andExpect(jsonPath("$.load_date", is(APPLICATION_LOAD_DATE_100)))
                .andExpect(jsonPath("$.status", is(STATUS_PENDING)));
    }

    @Test
    public void relaunchWfmExecution_OK() throws Exception {

        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setId(APPLICATION_ID_100);
        wfmExecutionResource.setStatus(APPLICATION_STATUS_RELAUNCH);
        wfmExecutionResource.setExecutionId(APPLICATION_ID_102);
        wfmExecutionResource.setStateBegin(STOPPED);
        

        mockMvc.perform(post("/wfm/job/relaunch").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.environment", is(APPLICATION_ENV_100)))
                .andExpect(jsonPath("$.id", not(APPLICATION_ID_100)))
                .andExpect(jsonPath("$.scope", is(APPLICATION_SCOPE_100)))
                .andExpect(jsonPath("$.workflow", is(APPLICATION_WORKFLOW_100)))
                .andExpect(jsonPath("$.load_date", is(APPLICATION_LOAD_DATE_100)))
                .andExpect(jsonPath("$.execution_date", is(APPLICATION_EXECUTION_DATE_100)))
                .andExpect(jsonPath("$.state_begin", is("START")));
    }

    @Test
    public void relaunchWfmExecution_OK_state_begin() throws Exception {

        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setId(APPLICATION_ID_100);
        wfmExecutionResource.setStateBegin("MYSTATE");
        wfmExecutionResource.setExecutionId(APPLICATION_ID_102);
        

        mockMvc.perform(post("/wfm/job/relaunch").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.environment", is(APPLICATION_ENV_100)))
                .andExpect(jsonPath("$.id", not(APPLICATION_ID_100)))
                .andExpect(jsonPath("$.state_begin", is("START")))
                .andExpect(jsonPath("$.scope", is(APPLICATION_SCOPE_100)))
                .andExpect(jsonPath("$.workflow", is(APPLICATION_WORKFLOW_100)))
                .andExpect(jsonPath("$.load_date", is(APPLICATION_LOAD_DATE_100)))
                .andExpect(jsonPath("$.execution_date", is(APPLICATION_EXECUTION_DATE_100)));
    }


    @Test
    public void relaunchWfmExecution_KO_noId() throws Exception {

        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();

        mockMvc.perform(post("/wfm/job/relaunch").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Identified of the execution to resume is mandatory")));
    }

    @Test
    public void resumeWfmExecution_KO_noId() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        

        mockMvc.perform(post("/wfm/job/resume").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Identified of the execution to resume is mandatory")));
    }

    @Test
    public void relaunchWfmExecution_KO_noStatus() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setId(APPLICATION_ID_101);
        wfmExecutionResource.setExecutionId(APPLICATION_ID_100.intValue());


        mockMvc.perform(post("/wfm/job/relaunch").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
        )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("This job is working, it is status REQUESTED_STOP")));
    }

    @Test
    public void relaunchWfmExecution_KO_noExecution() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setId(APPLICATION_ID_100);
        wfmExecutionResource.setExecutionId(425435);

        mockMvc.perform(post("/wfm/job/relaunch").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Cannot find the node execution identified by 425435")));
    }

    @Test
    public void resumeWfmExecution_KO_noExecution() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setId(APPLICATION_ID_100);
        wfmExecutionResource.setExecutionId(425435);
        

        mockMvc.perform(post("/wfm/job/resume").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Cannot find the node execution identified by 425435")));
    }

    @Test
    public void resumeWfmExecution_KO_noExecutionId() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setId(425435L);


        mockMvc.perform(post("/wfm/job/resume").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
        )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Identified of the execution to resume is mandatory")));
    }


    /**
     * Test negative case No target or Feed
     *
     * @throws Exception
     */
    @Test
    public void createWfmExecution_KO_NO_TARGET_OR_FEED() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setEnvironment(APPLICATION_ENV);
        wfmExecutionResource.setScope(APPLICATION_SCOPE);

        wfmExecutionResource.setWorkflow(APPLICATION_WORKFLOW_1);
        wfmExecutionResource.setLoadDate(APPLICATION_LOAD_DATE);
        wfmExecutionResource.setExecutionDate(APPLICATION_EXECUTION_DATE);
        

        mockMvc.perform(post("/wfm/job").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Feed or Target must be informed\n")));
    }

    /**
     * Test negative case with a null Environment
     *
     * @throws Exception
     */
    @Test
    public void createWfmExecution_KO_ENV_NULL() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setScope(APPLICATION_SCOPE);
        wfmExecutionResource.setFeed(APPLICATION_FEED);
        wfmExecutionResource.setWorkflow(APPLICATION_WORKFLOW_1);
        wfmExecutionResource.setLoadDate(APPLICATION_LOAD_DATE);
        wfmExecutionResource.setExecutionDate(APPLICATION_EXECUTION_DATE);
        

        mockMvc.perform(post("/wfm/job").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Environment cannot be empty or null\n")));
    }


    /**
     * Test negative case with a null Scope
     *
     * @throws Exception
     */
    @Test
    public void createWfmExecution_KO_SCOPE_NULL() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setEnvironment(APPLICATION_ENV);
        wfmExecutionResource.setFeed(APPLICATION_FEED);
        wfmExecutionResource.setWorkflow(APPLICATION_WORKFLOW_1);
        wfmExecutionResource.setLoadDate(APPLICATION_LOAD_DATE);
        wfmExecutionResource.setExecutionDate(APPLICATION_EXECUTION_DATE);
        

        mockMvc.perform(post("/wfm/job").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Scope cannot be empty or null\n")));
    }


    /**
     * Test negative case with a bad Load_date format
     *
     * @throws Exception
     */
    @Test
    public void createWfmExecution_KO_LOAD_DATE_FORMAT() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setEnvironment(APPLICATION_ENV);
        wfmExecutionResource.setScope(APPLICATION_SCOPE);
        wfmExecutionResource.setFeed(APPLICATION_FEED);
        wfmExecutionResource.setWorkflow(APPLICATION_WORKFLOW_1);
        wfmExecutionResource.setLoadDate("2016");
        wfmExecutionResource.setExecutionDate(APPLICATION_EXECUTION_DATE);
        

        mockMvc.perform(post("/wfm/job").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Load Date must have the format \"yyyyMMdd\", \"yyyy-MM-dd\"\n")));
    }


    /**
     * Test negative case with a bad Load_date format
     *
     * @throws Exception
     */
    @Test
    public void createWfmExecution_KO_EXECUTION_DATE_FORMAT() throws Exception {
        WfmExecutionResource wfmExecutionResource = new WfmExecutionResource();
        wfmExecutionResource.setEnvironment(APPLICATION_ENV);
        wfmExecutionResource.setScope(APPLICATION_SCOPE);
        wfmExecutionResource.setFeed(APPLICATION_FEED);
        wfmExecutionResource.setWorkflow(APPLICATION_WORKFLOW_1);
        wfmExecutionResource.setLoadDate(APPLICATION_LOAD_DATE);
        wfmExecutionResource.setExecutionDate("2016");
        

        mockMvc.perform(post("/wfm/job").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(wfmExecutionResource)).header("Authorization", "Basic DUMMY")
                )//NO CSRF
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Execution Date must have the format \"yyyyMMddHHmmss\"\n")));
    }

}
