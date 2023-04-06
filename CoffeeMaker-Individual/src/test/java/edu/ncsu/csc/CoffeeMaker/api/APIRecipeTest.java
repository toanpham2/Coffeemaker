package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests APIRecipe controller
 *
 * @author tgpham
 * @author jncoppet
 * @author mpwarren
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /** web application context for testing */
    @Autowired
    private WebApplicationContext context;

    /** recipe service for testing */
    @Autowired
    private RecipeService         service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Makes surea recipe can be posted
     *
     * @throws Exception
     *             if there is a problem posting
     */
    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe r = new Recipe();
        r.addIngredient( new Ingredient( "Chocolate", 5 ) );
        r.addIngredient( new Ingredient( "Milk", 4 ) );
        r.addIngredient( new Ingredient( "Coffee", 3 ) );
        r.addIngredient( new Ingredient( "Sugar", 8 ) );
        r.setPrice( 10 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    /**
     * Test posting a new recipe
     *
     * @throws Exception
     *             if there is a problem posting
     */
    @Test
    @Transactional
    public void testRecipeAPI () throws Exception {

        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( new Ingredient( "Chocolate", 10 ) );
        recipe.addIngredient( new Ingredient( "Milk", 20 ) );
        recipe.addIngredient( new Ingredient( "Coffee", 1 ) );
        recipe.addIngredient( new Ingredient( "Sugar", 5 ) );

        recipe.setPrice( 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    /**
     * Test adding recipe with a duplicate name
     *
     * @throws Exception
     *             if there is a problem in posting
     */
    @Test
    @Transactional
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    /**
     * Test adding more than 3 recipes when the limit is 3
     *
     * @throws Exception
     *             if there is a problem posting
     */
    @Test
    @Transactional
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 0, 2, 1, 2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    /**
     * Test deleting a recipe
     *
     * @throws UnsupportedEncodingException
     *             if there is an encoding exception
     * @throws Exception
     *             if there is a problem getting or posting
     */
    @Test
    @Transactional
    public void testDeleteRecipe1 () throws UnsupportedEncodingException, Exception {
        final String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        if ( !recipe.contains( "SpecialMilk" ) ) {
            // create a new Mocha recipe
            final Recipe r = new Recipe();
            r.addIngredient( new Ingredient( "Chocolate", 50 ) );
            r.addIngredient( new Ingredient( "Milk", 30 ) );
            r.addIngredient( new Ingredient( "Coffee", 90 ) );
            r.addIngredient( new Ingredient( "Sugar", 20 ) );
            r.setName( "SpecialMilk" );
            r.setPrice( 50 );
            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        }
        mvc.perform( delete( "/api/v1/recipes/SpecialMilk" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "Recipe object" ) ) ).andExpect( status().isOk() );
        assertFalse( recipe.contains( "SpecialMilk" ) );
        Assertions.assertEquals( 0, service.count(), "Recipe should be deleted" );

    }

    /**
     * Test deleting multiple recipes
     *
     * @throws UnsupportedEncodingException
     *             if there is an encoding issue
     * @throws Exception
     *             if there is a problem getting or deleting
     */
    @Test
    @Transactional
    public void testDeleteRecipe2 () throws UnsupportedEncodingException, Exception {
        final String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        if ( !recipe.contains( "Mocha" ) ) {
            // create a new Mocha recipe
            final Recipe r = new Recipe();
            r.addIngredient( new Ingredient( "Chocolate", 5 ) );
            r.addIngredient( new Ingredient( "Milk", 3 ) );
            r.addIngredient( new Ingredient( "Coffee", 9 ) );
            r.addIngredient( new Ingredient( "Sugar", 2 ) );
            r.setName( "Mocha" );
            r.setPrice( 5 );
            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        }
        if ( !recipe.contains( "SpecialMilk" ) ) {
            // create a new Mocha recipe
            final Recipe r1 = new Recipe();
            r1.addIngredient( new Ingredient( "Chocolate", 50 ) );
            r1.addIngredient( new Ingredient( "Milk", 30 ) );
            r1.addIngredient( new Ingredient( "Coffee", 90 ) );
            r1.addIngredient( new Ingredient( "Sugar", 20 ) );
            r1.setName( "SpecialMilk" );
            r1.setPrice( 50 );
            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );
        }
        // delete multiple recipes
        mvc.perform( delete( "/api/v1/recipes/Mocha" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "Recipe object" ) ) ).andExpect( status().isOk() );
        assertFalse( recipe.contains( "Mocha" ) );
        Assertions.assertEquals( 1, service.count(), "Recipe should be deleted" );

        mvc.perform( delete( "/api/v1/recipes/SpecialMilk" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "Recipe object" ) ) ).andExpect( status().isOk() );
        assertFalse( recipe.contains( "SpecialMilk" ) );
        Assertions.assertEquals( 0, service.count(), "Recipe should be deleted" );

    }

    /**
     * Test deleting recipes not found
     *
     * @throws UnsupportedEncodingException
     *             if there is an encoding issue
     * @throws Exception
     *             if there is a problem with the API calls
     */
    @Test
    @Transactional
    public void testDeleteRecipe3 () throws UnsupportedEncodingException, Exception {
        final String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        if ( !recipe.contains( "Default Coffee" ) ) {
            // create a new Default Coffee recipe
            final Recipe r = new Recipe();
            r.addIngredient( new Ingredient( "Chocolate", 50 ) );
            r.addIngredient( new Ingredient( "Milk", 1 ) );
            r.addIngredient( new Ingredient( "Coffee", 3 ) );
            r.addIngredient( new Ingredient( "Sugar", 1 ) );
            r.setName( "Default Coffee" );
            r.setPrice( 8 );
            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        }

        mvc.perform( delete( "/api/v1/recipes/SpecialMilk" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "Recipe object" ) ) ).andExpect( status().isNotFound() );

        if ( !recipe.contains( "SpecialMilk" ) ) {
            // create a new Mocha recipe
            final Recipe r1 = new Recipe();
            r1.addIngredient( new Ingredient( "Chocolate", 50 ) );
            r1.addIngredient( new Ingredient( "Milk", 30 ) );
            r1.addIngredient( new Ingredient( "Coffee", 90 ) );
            r1.addIngredient( new Ingredient( "Sugar", 20 ) );
            r1.setName( "SpecialMilk" );
            r1.setPrice( 50 );
            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );
        }

        Assertions.assertEquals( 2, service.count(), "Both recipes should remain." );

    }

    /**
     * Test deleting recipe
     *
     * @throws UnsupportedEncodingException
     *             if there is an encoding issue
     * @throws Exception
     *             if there is a problem while making API calls
     */
    @Test
    @Transactional
    public void testDeleteRecipe4 () throws UnsupportedEncodingException, Exception {
        final String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        if ( !recipe.contains( "Mocha" ) ) {
            // create a new Mocha recipe
            final Recipe r = new Recipe();
            r.addIngredient( new Ingredient( "Chocolate", 5 ) );
            r.addIngredient( new Ingredient( "Milk", 3 ) );
            r.addIngredient( new Ingredient( "Coffee", 9 ) );
            r.addIngredient( new Ingredient( "Sugar", 2 ) );
            r.setName( "Mocha" );
            r.setPrice( 5 );
            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        }

        mvc.perform( delete( "/api/v1/recipes/SpecialMilk" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( "Recipe object" ) ) ).andExpect( status().isNotFound() );

        Assertions.assertEquals( 1, service.count(), "Recipe should be deleted" );

    }

    /**
     * Test updating a recipe
     *
     * @throws Exception
     *             if there is a problem with API calls
     */
    @Test
    @Transactional
    public void testEditRecipe () throws Exception {
        final Recipe r = new Recipe();
        r.addIngredient( new Ingredient( "Chocolate", 50 ) );
        r.addIngredient( new Ingredient( "Milk", 1 ) );
        r.addIngredient( new Ingredient( "Coffee", 3 ) );
        r.addIngredient( new Ingredient( "Sugar", 1 ) );
        r.setName( "Default Coffee" );
        r.setPrice( 5 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

        final Recipe r1 = new Recipe();
        r1.setPrice( 15 );
        r1.addIngredient( new Ingredient( "Coffee", 5 ) );
        r1.addIngredient( new Ingredient( "Sugar", 3 ) );
        r1.addIngredient( new Ingredient( "Whipped Cream", 4 ) );

        mvc.perform( put( "/api/v1/recipes/Default Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

        final String dbrecipe = mvc.perform( get( "/api/v1/recipes/Default Coffee" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertFalse( dbrecipe.contains( "\"amount\":50,\"name\":\"Chocolate\"" ) );
        assertTrue( dbrecipe.contains( "\"amount\":5,\"name\":\"Coffee\"" ) );
        assertTrue( dbrecipe.contains( "\"amount\":3,\"name\":\"Sugar\"" ) );
        assertTrue( dbrecipe.contains( "\"amount\":4,\"name\":\"Whipped Cream\"" ) );
        assertFalse( dbrecipe.contains( "\"amount\":1,\"name\":\"Milk\"" ) );

    }

    /**
     * Helper method to create a recipe
     *
     * @param name
     *            name of recipe
     * @param price
     *            price of the recipe
     * @param coffee
     *            amount of coffee
     * @param milk
     *            amount of milk
     * @param sugar
     *            amount of sugar
     * @param chocolate
     *            amount of chocolate
     * @return the created recipe
     */
    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Recipe recipe = new Recipe();
        recipe.addIngredient( new Ingredient( "Chocolate", chocolate ) );
        recipe.addIngredient( new Ingredient( "Milk", milk ) );
        recipe.addIngredient( new Ingredient( "Coffee", coffee ) );
        recipe.addIngredient( new Ingredient( "Sugar", sugar ) );
        recipe.setName( name );
        recipe.setPrice( price );

        return recipe;
    }

}
