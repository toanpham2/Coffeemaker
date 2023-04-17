package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
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

    /** ingredient service used for testing */
    @Autowired
    private IngredientService     ingService;

    /** inventory service used for testing */
    @Autowired
    private InventoryService      inService;

    /** user service for testing */
    @Autowired
    private UserService           userService;

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
        userService.deleteAll();
        inService.deleteAll();
        ingService.deleteAll();
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
        final Ingredient coffee = new Ingredient( "Coffee", 10 );
        final Ingredient milk = new Ingredient( "Milk", 10 );
        final Ingredient sugar = new Ingredient( "Sugar", 10 );
        final Ingredient chocolate = new Ingredient( "Chocolate", 10 );
        r1.addIngredient( coffee );
        r1.addIngredient( milk );
        r1.addIngredient( sugar );
        r1.addIngredient( chocolate );
        r1.setPrice( 1 );

        final User user1 = new User( "username123", "pass123", "n/a", false, false );
        final User user2 = new User( "username222", "pass222", "n/a", false, false );
        final User user3 = new User( "username333", "pass222", "n/a", false, false );
        final CoffeeOrder order1 = new CoffeeOrder( r1, user1.getUsername() );
        final CoffeeOrder order2 = new CoffeeOrder( r1, user2.getUsername() );
        final CoffeeOrder order3 = new CoffeeOrder( r1, user3.getUsername() );
        final List<Ingredient> ingList = new ArrayList<Ingredient>();
        ingList.add( chocolate );
        ingList.add( coffee );
        ingList.add( sugar );
        mvc.perform( put( "/api/v1/inventory/Chocolate" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( chocolate ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Milk" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( milk ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory/Sugar" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sugar ) ) ).andExpect( status().isOk() );

        // Inventory i = mvc.perform( get( "/api/v1/inventory" ) ).andDo(
        // print() ).andExpect( status().isOk() ).andReturn().;
        userService.save( user1 );
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

        mvc.perform( get( "/api/v1/orders" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();
        mvc.perform( get( "/api/v1/orders/username133" ) ).andDo( print() ).andExpect( status().isNotFound() )
                .andReturn().getResponse().getContentAsString();
        final Long id = service.findByName( "username123" ).getId();

        mvc.perform( delete( "/api/v1/orders/" + id.toString() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();
        // service
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) );
        order1.setisFulfilled( true );
        mvc.perform( put( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );

        final String fulfilledOrder = mvc.perform( get( "/api/v1/orders/username123" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertTrue( fulfilledOrder.contains( "\"isFulfilled\":true,\"isPickedUp\":false" ) );
        order1.setisPickedUp( true );
        mvc.perform( put( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );

        final String pickedupOrder = mvc.perform( get( "/api/v1/orders/username123" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertTrue( pickedupOrder.contains( "\"isFulfilled\":true,\"isPickedUp\":true" ) );

        mvc.perform( put( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order3 ) ) ).andExpect( status().isNotFound() );

    }
}
