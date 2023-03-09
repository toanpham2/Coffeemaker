package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIIngredientTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IngredientService     service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    public void ensureIngredient () throws Exception {

        final Ingredient coffee = new Ingredient( "Coffee", 10 );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    @Test
    @Transactional
    public void testAddIngredient2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Ingredients in the CoffeeMaker" );
        final Ingredient r1 = new Ingredient( "Coffee", 10 );

        service.save( r1 );

        final Ingredient r2 = new Ingredient( "Coffee", 10 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should be 2 ingredients in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testGetIngredient () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "Should be 0 ingredients" );

        final Ingredient r1 = new Ingredient( "Coffee", 10 );
        service.save( r1 );
        final Ingredient r2 = new Ingredient( "Milk", 10 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(), "Should be 2 ingredients" );

        String ingredients = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( ingredients.contains( "Coffee" ) );
        assertTrue( ingredients.contains( "Milk" ) );

        ingredients = mvc.perform( get( "/api/v1/ingredients/{name}", "Milk" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertFalse( ingredients.contains( "Coffee" ) );
        assertTrue( ingredients.contains( "Milk" ) );

    }

    @Test
    @Transactional
    public void testDeleteIngredient () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "Should be 0 ingredients" );

        final Ingredient r1 = new Ingredient( "Coffee", 10 );
        service.save( r1 );
        final Ingredient r2 = new Ingredient( "Milk", 10 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(), "Should be 2 ingredients" );

        final Long id = service.findByName( "Coffee" ).getId();

        mvc.perform( delete( "/api/v1/ingredients/" + id.toString() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "Recipe object" ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.count(), "Should be 1 ingredient" );

    }

    @Test
    @Transactional
    public void testUpdateIngredient () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "Should be 0 ingredients" );

        final Ingredient r1 = new Ingredient( "Coffee", 10 );
        service.save( r1 );
        final Ingredient r2 = new Ingredient( "Milk", 12 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(), "Should be 2 ingredients" );

        final Ingredient r3 = new Ingredient( "Coffee", 15 );
        service.save( r1 );

        mvc.perform( put( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r3 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, service.count(), "Should be 2 ingredients" );

        final String ingredients = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertFalse( ingredients.contains( "10" ) );
        assertTrue( ingredients.contains( "25" ) );
    }

}
