package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    @Autowired
    private RecipeService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testAddRecipe () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 1 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
    }

    @Test
    @Transactional
    public void testNoRecipes () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        try {
            final Recipe r1 = new Recipe();
            r1.setName( "Tasty Drink" );
            final Ingredient Coffee = new Ingredient( "Coffee", -12 );
            final Ingredient Milk = new Ingredient( "Milk", 0 );
            final Ingredient Sugar = new Ingredient( "Sugar", 0 );
            final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
            r1.addIngredient( Coffee );
            r1.addIngredient( Milk );
            r1.addIngredient( Sugar );
            r1.addIngredient( Chocolate );
            r1.setPrice( 12 );
            service.save( r1 );

            final Recipe r2 = new Recipe();
            r2.setName( "Mocha" );
            r2.setPrice( 1 );
            final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
            final Ingredient Milk2 = new Ingredient( "Milk", 1 );
            final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
            final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
            r2.addIngredient( Coffee2 );
            r2.addIngredient( Milk2 );
            r2.addIngredient( Sugar2 );
            r2.addIngredient( Chocolate2 );
            service.save( r2 );

            final List<Recipe> recipes = List.of( r1, r2 );
            service.saveAll( recipes );
            Assertions.assertEquals( 0, service.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof IllegalArgumentException );
        }

    }

    @Test
    @Transactional
    public void testAddRecipe1 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = new Recipe();
        r1.setName( name );
        final Ingredient Coffee = new Ingredient( "Coffee", 12 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );

        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /* Test2 is done via the API for different validation */

    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        // final Recipe r1 = createRecipe( name, -50, 3, 1, 1, 0 );
        final Recipe r1 = new Recipe();
        r1.setName( name );
        final Ingredient Coffee = new Ingredient( "Coffee", 5 );
        final Ingredient Milk = new Ingredient( "Milk", 3 );
        final Ingredient Sugar = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( -12 );
        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative price" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        // final Recipe r1 = createRecipe( name, 50, -3, 1, 1, 2 );
        final Recipe r1 = new Recipe();
        r1.setName( name );

        try {
            final Ingredient Coffee = new Ingredient( "Coffee", -15 );
            final Ingredient Milk = new Ingredient( "Milk", 3 );
            final Ingredient Sugar = new Ingredient( "Sugar", 1 );
            final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
            r1.addIngredient( Coffee );
            r1.addIngredient( Milk );
            r1.addIngredient( Sugar );
            r1.addIngredient( Chocolate );
            r1.setPrice( 12 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of coffee" );
        }
        catch ( final IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        // final Recipe r1 = createRecipe( name, 50, 3, -1, 1, 2 );
        final Recipe r1 = new Recipe();
        r1.setName( name );

        try {
            final Ingredient Coffee = new Ingredient( "Coffee", 15 );
            final Ingredient Milk = new Ingredient( "Milk", -3 );
            final Ingredient Sugar = new Ingredient( "Sugar", 1 );
            final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
            r1.addIngredient( Coffee );
            r1.addIngredient( Milk );
            r1.addIngredient( Sugar );
            r1.addIngredient( Chocolate );
            r1.setPrice( 12 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of milk" );
        }
        catch ( final IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe6 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        // final Recipe r1 = createRecipe( name, 50, 3, 1, -1, 2 );
        final Recipe r1 = new Recipe();
        r1.setName( name );

        try {
            final Ingredient Coffee = new Ingredient( "Coffee", 15 );
            final Ingredient Milk = new Ingredient( "Milk", 3 );
            final Ingredient Sugar = new Ingredient( "Sugar", -1 );
            final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
            r1.addIngredient( Coffee );
            r1.addIngredient( Milk );
            r1.addIngredient( Sugar );
            r1.addIngredient( Chocolate );
            r1.setPrice( 12 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of sugar" );
        }
        catch ( final IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe7 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        // final Recipe r1 = createRecipe( name, 50, 3, 1, 1, -2 );
        final Recipe r1 = new Recipe();
        r1.setName( name );

        try {
            final Ingredient Coffee = new Ingredient( "Coffee", 15 );
            final Ingredient Milk = new Ingredient( "Milk", 3 );
            final Ingredient Sugar = new Ingredient( "Sugar", 1 );
            final Ingredient Chocolate = new Ingredient( "Chocolate", -2 );
            r1.addIngredient( Coffee );
            r1.addIngredient( Milk );
            r1.addIngredient( Sugar );
            r1.addIngredient( Chocolate );
            r1.setPrice( 12 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of chocolate" );
        }
        catch ( final IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe13 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        // service.save( r1 );
        // final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        // service.save( r2 );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(),
                "Creating two recipes should result in two recipes in the database" );

    }

    @Test
    @Transactional
    public void testAddRecipe14 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        // service.save( r1 );
        // final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        // service.save( r2 );
        // final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        // service.save( r3 );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );

        final Recipe r3 = new Recipe();
        r3.setName( "newMocha" );
        r3.setPrice( 1 );
        final Ingredient Coffee3 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk3 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar3 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate3 = new Ingredient( "Chocolate", 1 );
        r3.addIngredient( Coffee3 );
        r3.addIngredient( Milk3 );
        r3.addIngredient( Sugar3 );
        r3.addIngredient( Chocolate3 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        // service.save( r1 );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 3 );
        final Ingredient Milk = new Ingredient( "Milk", 1 );
        final Ingredient Sugar = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        // service.save( r1 );
        // final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        // service.save( r2 );
        // final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        // service.save( r3 );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );

        final Recipe r3 = new Recipe();
        r3.setName( "newMocha" );
        r3.setPrice( 1 );
        final Ingredient Coffee3 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk3 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar3 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate3 = new Ingredient( "Chocolate", 1 );
        r3.addIngredient( Coffee3 );
        r3.addIngredient( Milk3 );
        r3.addIngredient( Sugar3 );
        r3.addIngredient( Chocolate3 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // final Recipe r1 = createRecipe( "Coffee 1", 50, 3, 1, 1, 0 );
        // service.save( r1 );
        // Assertions.assertEquals( 1, service.count(), "There should be one
        // recipe in the database" );
        //
        // final Recipe r2 = createRecipe( "Coffee 2", 50, 3, 3, 3, 3 );
        // service.save( r2 );
        // Assertions.assertEquals( 2, service.count(), "There should be two
        // recipes in the database" );
        //
        // final Recipe r3 = createRecipe( "Coffee 3", 60, 1, 1, 1, 0 );
        // service.save( r3 );
        // Assertions.assertEquals( 3, service.count(), "There should be three
        // recipes in the database" );
        //
        // final Recipe r4 = createRecipe( "Coffee 4", 55, 5, 2, 1, 1 );
        // service.save( r4 );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );

        final Recipe r3 = new Recipe();
        r3.setName( "newMocha" );
        r3.setPrice( 1 );
        final Ingredient Coffee3 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk3 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar3 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate3 = new Ingredient( "Chocolate", 1 );
        r3.addIngredient( Coffee3 );
        r3.addIngredient( Milk3 );
        r3.addIngredient( Sugar3 );
        r3.addIngredient( Chocolate3 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be four recipes in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 2, service.findAll().size(), "There should be 3 recipes in the CoffeeMaker" );

        service.delete( r2 );
        Assertions.assertEquals( 1, service.findAll().size(), "There should be 2 recipes in the CoffeeMaker" );

        service.delete( r3 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be 1 recipes in the CoffeeMaker" );

        // service.delete( r4 );
        // Assertions.assertEquals( 0, service.findAll().size(), "There should
        // be 0 recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // final Recipe r1 = createRecipe( "Coffee 1", 50, 3, 1, 1, 0 );
        // service.save( r1 );
        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );
        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be 0 recipes in the CoffeeMaker" );

        // final Recipe r2 = createRecipe( "Coffee 2", 50, 3, 3, 3, 3 );
        // service.save( r2 );
        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );
        Assertions.assertEquals( 1, service.count(), "There should be two recipes in the database" );

        // final Recipe r3 = createRecipe( "Coffee 3", 60, 1, 1, 1, 0 );
        // service.save( r3 );
        final Recipe r3 = new Recipe();
        r3.setName( "newMocha" );
        r3.setPrice( 1 );
        final Ingredient Coffee3 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk3 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar3 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate3 = new Ingredient( "Chocolate", 1 );
        r3.addIngredient( Coffee3 );
        r3.addIngredient( Milk3 );
        r3.addIngredient( Sugar3 );
        r3.addIngredient( Chocolate3 );
        service.save( r3 );
        Assertions.assertEquals( 2, service.count(), "There should be three recipes in the database" );

        service.delete( r2 );
        Assertions.assertEquals( 1, service.findAll().size(), "There should be 1 recipe in the CoffeeMaker" );

        // final Recipe r4 = createRecipe( "Coffee 4", 55, 5, 2, 1, 1 );
        // service.save( r4 );
        // Assertions.assertEquals( 2, service.count(), "There should be four
        // recipes in the database" );

        service.delete( r3 );
        // service.delete( r4 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be 0 recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // final Recipe r1 = createRecipe( "Coffee 1", 50, 3, 1, 1, 0 );
        // service.save( r1 );
        // Assertions.assertEquals( 1, service.count(), "There should be one
        // recipe in the database" );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );
        // final Recipe r2 = createRecipe( "Coffee 2", 50, 3, 3, 3, 3 );
        // service.save( r2 );
        Assertions.assertEquals( 2, service.count(), "There should be two recipes in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 1, service.findAll().size(), "There should be 1 recipe in the CoffeeMaker" );

        service.save( r1 );
        Assertions.assertEquals( 2, service.count(), "There should be one recipe in the database" );

        service.delete( r2 );
        Assertions.assertEquals( 1, service.findAll().size(), "There should be 1 recipe in the CoffeeMaker" );

        service.save( r2 );
        Assertions.assertEquals( 2, service.count(), "There should be 2 recipes in the database" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe6 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // final Recipe r1 = createRecipe( "Coffee 1", 50, 3, 1, 1, 0 );
        // service.save( r1 );
        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );
        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be 1 recipe in the CoffeeMaker" );

        // final Recipe r2 = createRecipe( "Coffee 2", 50, 3, 3, 3, 3 );
        // service.save( r2 );
        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );
        Assertions.assertEquals( 1, service.count(), "There should be two recipes in the database" );

        service.delete( r2 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be 1 recipe in the CoffeeMaker" );

        // final Recipe r3 = createRecipe( "Coffee 3", 60, 1, 1, 1, 0 );
        // service.save( r3 );
        final Recipe r3 = new Recipe();
        r3.setName( "newMocha" );
        r3.setPrice( 1 );
        final Ingredient Coffee3 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk3 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar3 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate3 = new Ingredient( "Chocolate", 1 );
        r3.addIngredient( Coffee3 );
        r3.addIngredient( Milk3 );
        r3.addIngredient( Sugar3 );
        r3.addIngredient( Chocolate3 );
        service.save( r3 );
        Assertions.assertEquals( 1, service.count(), "There should be three recipes in the database" );

        service.delete( service.findById( r3.getId() ) );
        Assertions.assertEquals( 0, service.count(), "There should be three recipes in the database" );

    }

    @Test
    @Transactional
    public void testEditRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        // service.save( r1 );
        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );
        r1.setPrice( 70 );

        service.save( r1 );

        final Recipe retrieved = service.findByName( "Tasty Drink" );

        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        // Assertions.assertEquals( 3, (int) retrieved.getCoffee() );
        // Assertions.assertEquals( 1, (int) retrieved.getMilk() );
        // Assertions.assertEquals( 1, (int) retrieved.getSugar() );
        // Assertions.assertEquals( 0, (int) retrieved.getChocolate() );

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    // private Recipe createRecipe ( final String name, final Integer price,
    // final Integer coffee, final Integer milk,
    // final Integer sugar, final Integer chocolate ) {
    // final Recipe recipe = new Recipe();
    // recipe.setName( name );
    // recipe.setPrice( price );
    // recipe.setCoffee( coffee );
    // recipe.setMilk( milk );
    // recipe.setSugar( sugar );
    // recipe.setChocolate( chocolate );
    //
    // return recipe;
    // }

    @Test
    @Transactional
    public void testToString () {

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );
        assertEquals(
                "Ingredient [id=" + "" + r1.getOneIngredient( "Coffee" ).getId() + ", amount=1, type=Coffee]"
                        + " Ingredient [id=" + "" + r1.getOneIngredient( "Milk" ).getId() + ""
                        + ", amount=0, type=Milk]" + " Ingredient [id=" + "" + r1.getOneIngredient( "Sugar" ).getId()
                        + "" + ", amount=0, type=Sugar] Ingredient [id=" + ""
                        + r1.getOneIngredient( "Chocolate" ).getId() + "" + ", amount=0, type=Chocolate] ",
                r1.toString() );

    }

    @Test
    @Transactional
    public void testEqual () {
        // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        // final Recipe r2 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        // final Recipe r3 = createRecipe( "NotCoffee", 50, 3, 1, 1, 0 );
        // final Recipe r4 = createRecipe( null, 50, 3, 1, 1, 0 );
        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Tasty Drink" );
        r2.setPrice( 12 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );

        final Recipe r3 = new Recipe();
        r3.setName( "newMocha" );
        r3.setPrice( 1 );
        final Ingredient Coffee3 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk3 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar3 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate3 = new Ingredient( "Chocolate", 1 );
        r3.addIngredient( Coffee3 );
        r3.addIngredient( Milk3 );
        r3.addIngredient( Sugar3 );
        r3.addIngredient( Chocolate3 );
        service.save( r3 );

        service.save( r1 );
        service.save( r2 );
        service.save( r3 );
        assertTrue( r1.equals( r2 ) );
        assertNotNull( r1 );
        assertFalse( r1.equals( r3 ) );
        // assertFalse( r4.equals( r3 ) );
    }

    @Test
    @Transactional
    public void testEqualRecipe () {
        // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        // final Recipe r2 = createRecipe( "Mocha", 50, 3, 2, 1, 1 );
        // final Recipe r3 = createRecipe( "NotCoffee", 50, 3, 1, 1, 0 );
        // final Recipe r4 = createRecipe( null, 50, 3, 1, 1, 0 );
        // final Recipe r5 = createRecipe( "Coffee2", 50, 3, 1, 1, 0 );
        // service.save( r1 );
        // service.save( r2 );
        // service.save( r3 );
        // service.save( r4 );
        // assertFalse( r1.checkRecipe() );
        // assertFalse( r2.checkRecipe() );
        // assertFalse( r3.checkRecipe() );
        // assertFalse( r4.checkRecipe() );
        // assertFalse( r5.checkRecipe() );
        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );

        final Recipe r3 = new Recipe();
        r3.setName( "newMocha" );
        r3.setPrice( 1 );
        final Ingredient Coffee3 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk3 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar3 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate3 = new Ingredient( "Chocolate", 1 );
        r3.addIngredient( Coffee3 );
        r3.addIngredient( Milk3 );
        r3.addIngredient( Sugar3 );
        r3.addIngredient( Chocolate3 );
        service.save( r3 );

        // final Recipe r6 = createRecipe( "Fake Coffee", 50, 0, 0, 0, 0 );
        // assertTrue( r6.checkRecipe() );

        // assertEquals( r6.hashCode(), r6.hashCode() );
        // assertNotEquals( r5.hashCode(), r6.hashCode() );
        // assertNotEquals( r4.hashCode(), r5.hashCode() );
        // assertNotEquals( r3.hashCode(), r4.hashCode() );
        assertNotEquals( r2.hashCode(), r3.hashCode() );
        assertNotEquals( r1.hashCode(), r2.hashCode() );
    }

    @Test
    @Transactional
    public void testExistsById () {
        // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        // final Recipe r2 = createRecipe( "Mocha", 50, 3, 2, 1, 1 );
        // final Recipe r3 = createRecipe( "NotCoffee", 50, 3, 1, 1, 0 );
        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 12 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 1 );
        r2.addIngredient( Coffee2 );
        r2.addIngredient( Milk2 );
        r2.addIngredient( Sugar2 );
        r2.addIngredient( Chocolate2 );
        service.save( r2 );

        final Recipe r3 = new Recipe();
        r3.setName( "newMocha" );
        r3.setPrice( 1 );
        final Ingredient Coffee3 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk3 = new Ingredient( "Milk", 1 );
        final Ingredient Sugar3 = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate3 = new Ingredient( "Chocolate", 1 );
        r3.addIngredient( Coffee3 );
        r3.addIngredient( Milk3 );
        r3.addIngredient( Sugar3 );
        r3.addIngredient( Chocolate3 );
        service.save( r3 );

        service.save( r1 );
        service.save( r2 );
        service.save( r3 );
        assertTrue( service.existsById( r1.getId() ) );
        assertTrue( service.existsById( r2.getId() ) );
        assertTrue( service.existsById( r3.getId() ) );
    }

    @Test
    @Transactional
    public void testEditRecipe () {
        final Recipe r1 = new Recipe();
        r1.setName( "Coffee" );
        final Ingredient Coffee = new Ingredient( "Coffee", 20 );
        final Ingredient Milk = new Ingredient( "Milk", 10 );
        final Ingredient Sugar = new Ingredient( "Sugar", 10 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 10 );

        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );

        Chocolate.setAmount( 10 );
        r1.updateIngredient( Chocolate );

        assertEquals( (Integer) 10, r1.getOneIngredient( "Chocolate" ).getAmount() );

        r1.removeIngredient( "Sugar" );
        assertEquals( 3, r1.getAllIngredients().size() );
    }
}
