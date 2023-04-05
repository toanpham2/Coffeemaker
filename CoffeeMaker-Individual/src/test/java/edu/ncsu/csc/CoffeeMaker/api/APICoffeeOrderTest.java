package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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
import edu.ncsu.csc.CoffeeMaker.models.CoffeeOrder;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Test API CoffeeOrder controller
 *
 * @author tgpham
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APICoffeeOrderTest {

    @Autowired
    private IngredientService     IngService;

    @Autowired
    private InventoryService      InService;

    /** user service for testing */
    @Autowired
    private UserService           UserService;

    /** user service for testing */
    @Autowired
    private RecipeService         recipeService;

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
    private OrderService          service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
        recipeService.deleteAll();
        UserService.deleteAll();
        InService.deleteAll();
        IngService.deleteAll();
    }

    /**
     * Test posting new user- staff
     *
     * @throws Exception
     *             if there are problems creating user
     */
    @Test
    @Transactional
    public void testCreatandGetOrder () throws Exception {
        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        final Ingredient Coffee = new Ingredient( "Coffee", 10 );
        final Ingredient Milk = new Ingredient( "Milk", 10 );
        final Ingredient Sugar = new Ingredient( "Sugar", 10 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 10 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 1 );

        final User user1 = new User( "username123", "pass123", "n/a" );
        final User user2 = new User( "username222", "pass222", "n/a" );

        final CoffeeOrder order1 = new CoffeeOrder( r1, user1 );
        final CoffeeOrder order2 = new CoffeeOrder( r1, user2 );
        final List<Ingredient> ingList = new ArrayList<Ingredient>();
        ingList.add( Chocolate );
        ingList.add( Coffee );
        ingList.add( Sugar );
        mvc.perform( put( "/api/v1/inventory/Chocolate" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( Chocolate ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Milk" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( Milk ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( Coffee ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Sugar" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( Sugar ) ) ).andExpect( status().isOk() );

        // Inventory i = mvc.perform( get( "/api/v1/inventory" ) ).andDo(
        // print() ).andExpect( status().isOk() ).andReturn().;
        UserService.save( user1 );
        recipeService.save( r1 );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) );

        final String orderName = mvc.perform( get( "/api/v1/orders/username123" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( orderName.contains( "username123" ) );

        Assertions.assertEquals( 1, service.count() ); // user is saved in
                                                       // service
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isConflict() ).andReturn()
                .getResponse().getContentAsString();
        final String orders = mvc.perform( get( "/api/v1/orders" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final String orderFalseGet = mvc.perform( get( "/api/v1/orders/username133" ) ).andDo( print() )
                .andExpect( status().isNotFound() ).andReturn().getResponse().getContentAsString();
        final Long id = service.findByName( "username123" ).getId();

        mvc.perform( delete( "/api/v1/orders/" + id.toString() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();
        // service
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) );
        mvc.perform( put( "/api/v1/ordersFulfilled" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );
        mvc.perform( put( "/api/v1/ordersPickedUp" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );
        mvc.perform( put( "/api/v1/ordersFulfilled" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ) ).andExpect( status().isNotFound() );
        mvc.perform( put( "/api/v1/ordersPickedUp" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ) ).andExpect( status().isNotFound() );
        final String orderName1 = mvc.perform( get( "/api/v1/orders/username123" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( orderName1.contains( "true" ) );
    }
}
