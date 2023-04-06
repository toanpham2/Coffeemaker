package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Test database interaction
 *
 * @author tgpham
 * @author jncoppet
 * @author mpwarren
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class TestDatabaseInteraction {

    /** recipe service for testing */
    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testRecipes () {
        final Recipe r = new Recipe();
        r.addIngredient( new Ingredient( "Chocolate", 1 ) );
        r.addIngredient( new Ingredient( "Milk", 2 ) );
        r.addIngredient( new Ingredient( "Coffee", 5 ) );
        r.addIngredient( new Ingredient( "Sugar", 1 ) );
        r.setName( "Coffee 1" );
        r.setPrice( 5 );
        // final Recipe r2 = new Recipe();
        // r2.setChocolate( 1 );
        // r2.setCoffee( 5 );
        // r2.setMilk( 2 );
        // r2.setName( "Coffee 2" );
        // r2.setPrice( 5 );
        // r2.setSugar( 1 );
        recipeService.save( r );
        // recipeService.save( r2 );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );
        // final Recipe dbRecipe2 = dbRecipes.get( 1 );

        assertEquals( r.getName(), dbRecipe.getName() );
        assertEquals( r.getOneIngredient( "Chocolate" ), dbRecipe.getOneIngredient( "Chocolate" ) );
        assertEquals( r.getOneIngredient( "Coffee" ), dbRecipe.getOneIngredient( "Coffee" ) );
        assertEquals( r.getOneIngredient( "Milk" ), dbRecipe.getOneIngredient( "Milk" ) );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );
        assertEquals( r.getOneIngredient( "Sugar" ), dbRecipe.getOneIngredient( "Sugar" ) );

        assertEquals( r, recipeService.findByName( "Coffee 1" ) );
        // assertEquals( r2, recipeService.findByName( "Coffee 2" ) );

        dbRecipe.setPrice( 15 );
        dbRecipe.getOneIngredient( "Sugar" ).setAmount( 12 );
        recipeService.save( dbRecipe );
        assertEquals( 15, (int) dbRecipe.getPrice() );
        assertEquals( 12, (int) dbRecipe.getOneIngredient( "Sugar" ).getAmount() );

        assertEquals( 1, dbRecipes.size() );
        assertEquals( 15, (int) recipeService.findAll().get( 0 ).getPrice() );
        assertEquals( 12, (int) recipeService.findAll().get( 0 ).getOneIngredient( "Sugar" ).getAmount() );
    }
}
