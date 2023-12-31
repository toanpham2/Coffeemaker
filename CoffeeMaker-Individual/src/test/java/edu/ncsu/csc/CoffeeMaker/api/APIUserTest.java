package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Test API user controller
 *
 * @author stnguye3
 * @author bcchavez
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIUserTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /** web application context */
    @Autowired
    private WebApplicationContext context;

    /** user service for testing */
    @Autowired
    private UserService           service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Test posting new user- staff
     *
     * @throws Exception
     *             if there are problems creating user
     */
    @Test
    @Transactional
    public void testAddStaff () throws Exception {
        final User user2 = new User( "staffuser4", "staffpass123", "OnePiece", true, false );

        // add new user (register)
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user2 ) ) );

        // get user info for staffuser4 (login)
        final String userInfo = mvc.perform( get( "/api/v1/users/staffuser4" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( userInfo.contains( "staffuser4" ) );

        Assertions.assertEquals( 1, service.count() ); // user is saved in
                                                       // service
    }

    /**
     * Test GET for user not in system
     *
     * @throws Exception
     *             if there are issues in getting user
     * @throws UnsupportedEncodingException
     *             if unsupported character encoding scheme is used
     */
    @Test
    @Transactional
    public void testGetUserNotFound () throws UnsupportedEncodingException, Exception {
        Assertions.assertEquals( 0, service.count() ); // no users saved

        final String userInfo = mvc.perform( get( "/api/v1/users/FakeUser11" ) ).andDo( print() )
                .andExpect( status().is4xxClientError() ).andReturn().getResponse().getContentAsString();

        // should not have any info about this unsaved user
        Assertions.assertFalse( userInfo.contains( "FakeUser11" ) );
    }

    /**
     * Test posting and getting new User - customer
     *
     * @throws Exception
     *             if there are issues in posting user
     */
    @Test
    @Transactional
    public void testAddCustomer () throws Exception {
        // first customer
        final User customer1 = new User( "syd123", "pass000", "onepiece", false, false );

        // save this customer
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer1 ) ) );

        Assertions.assertEquals( 1, service.count() ); // there is one user in
                                                       // the system

        final String userInfo = mvc.perform( get( "/api/v1/users/syd123" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        // should have correct password info
        Assertions.assertTrue( userInfo.contains( "pass000" ) );

        // create another customer with the same username
        final User customer2 = new User( "syd123", "word444", "N/A", false, false );

        // save this customer, should get status 409 conflict
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer2 ) ) ).andExpect( status().is4xxClientError() );
        // customer2 duplicate should not be saved
        Assertions.assertEquals( 1, service.count() );

    }

    /**
     * Test updating the user's order number with PUT call
     *
     * @throws Exception
     *             if there are issues with posting user
     */
    @Test
    @Transactional
    public void testUpdateOrderNumber () throws Exception {
        final User customer1 = new User( "user123", "pass5555", "onepiece", false, false );

        // save this customer
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer1 ) ) );

        Assertions.assertEquals( 1, service.count() ); // there is one user in
                                                       // the system

        // order number starts at 0
        Assertions.assertEquals( 0, customer1.getOrderNumber() );

        final String userInfo1 = mvc.perform( get( "/api/v1/users/user123" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        System.out.println( userInfo1.toString() );

        // increment order number by calling put
        mvc.perform( put( "/api/v1/users/user123" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer1.getUsername() ) ) ).andExpect( status().isOk() );

        // make sure order number is 1 now
        Assertions.assertEquals( 1, service.count(), "Should be just one user" );

        final String userInfo = mvc.perform( get( "/api/v1/users/user123" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertFalse( userInfo.contains( "\"orderNumber\":0" ) );
        Assertions.assertTrue( userInfo.contains( "\"orderNumber\":1" ) );
        // Assertions.assertEquals( 1, customer1.getOrderNumber() );
    }

    /**
     * Test deleting a current User
     *
     * @throws Exception
     *             if there are issues in deleting user
     */
    @Test
    @Transactional
    public void testDeleteCustomer () throws Exception {
        final User anonUser = new User( "syd123", "", "n/a", false, true );
        // save this customer
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( anonUser ) ) );
        // delete this user
        mvc.perform( delete( "/api/v1/users/syd123" ).contentType( MediaType.APPLICATION_JSON ) );
        mvc.perform( delete( "/api/v1/users/randomName" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( anonUser ) ) ).andExpect( status().isNotFound() );
        Assertions.assertEquals( 0, service.count() );
    }

}
