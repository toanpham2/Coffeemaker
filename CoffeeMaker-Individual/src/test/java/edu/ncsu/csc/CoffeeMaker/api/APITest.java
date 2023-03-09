package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService      service;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    @Test
    @Transactional
    public void testRecipe () throws Exception {
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
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
        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        assertTrue( recipe.contains( "Mocha" ) );

        final Inventory ivt = service.getInventory();
        final Ingredient chocolate = new Ingredient( "Chocolate", 15 );
        final Ingredient milk = new Ingredient( "Milk", 15 );
        final Ingredient coffee = new Ingredient( "Coffee", 15 );
        final Ingredient sugar = new Ingredient( "Sugar", 15 );

        mvc.perform( put( "/api/v1/inventory/Chocolate" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( chocolate ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Milk" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( milk ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Sugar" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sugar ) ) ).andExpect( status().isOk() );

        assertEquals( 4, ivt.getIngredients().size() );

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 100 ) ) ).andExpect( status().isOk() ).andDo( print() );

        String i = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        assertTrue( i.contains( "Chocolate" ) );

        i = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

    }

    @Test
    @Transactional
    public void testUpdateIngredients () throws Exception {
        final Inventory ivt = service.getInventory();
        final List<Ingredient> updateList = new ArrayList<Ingredient>();
        final Ingredient chocolate = new Ingredient( "Chocolate", 15 );
        updateList.add( chocolate );
        final Ingredient milk = new Ingredient( "Milk", 15 );
        updateList.add( milk );
        final Ingredient coffee = new Ingredient( "Coffee", 15 );
        updateList.add( coffee );
        final Ingredient sugar = new Ingredient( "Sugar", 15 );
        updateList.add( sugar );

        mvc.perform( put( "/api/v1/inventory/Chocolate" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( chocolate ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Milk" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( milk ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Sugar" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sugar ) ) ).andExpect( status().isOk() );

        String i = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( i.contains( "Chocolate" ) );
        assertTrue( i.contains( "Milk" ) );
        assertTrue( i.contains( "Sugar" ) );
        assertTrue( i.contains( "Coffee" ) );

        mvc.perform( put( "/api/v1/inventory/" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updateList ) ) ).andExpect( status().isOk() );

        i = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        int lastIndex = 0;
        int count = 0;
        while ( lastIndex != -1 ) {
            lastIndex = i.indexOf( "30", lastIndex );
            if ( lastIndex != -1 ) {
                count++;
                lastIndex += 2;

            }
        }
        assertTrue( count >= 4 );

    }

}
