package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * api controller for ingredients
 *
 * @author section 201 group 3
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIIngredientController extends APIController {

    /** the service for ingredients */
    @Autowired
    private IngredientService service;

    /**
     * gets an ingredeint from the database
     *
     * @param name
     *            the name of the ingredient to get
     * @return response entity
     */
    @GetMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity getIngredient ( @PathVariable final String name ) {

        final Ingredient ingr = service.findByName( name );

        if ( null == ingr ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( ingr, HttpStatus.OK );
    }

    /**
     * REST API method to provide GET access to all recipes in the system
     *
     * @return JSON representation of all recipies
     */
    @GetMapping ( BASE_PATH + "/ingredients" )
    public List<Ingredient> getIngredients () {
        return service.findAll();
    }

    /**
     * DELETE - deletes a specified ingredient from the database
     *
     * @param id
     *            the id to delete the name of the ingredient to delete
     * @return Response Entity
     */
    @DeleteMapping ( BASE_PATH + "/ingredients/{id}" )
    public ResponseEntity deleteIngredient ( @PathVariable final Long id ) {
        System.out.println( "Deleting id " + id.toString() );
        final Ingredient ing = service.findById( id );
        if ( null == ing ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + ing.getType() ),
                    HttpStatus.NOT_FOUND );
        }
        service.delete( ing );

        return new ResponseEntity( successResponse( ing.getType() + " was deleted successfully" ), HttpStatus.OK );
    }

    /**
     * POST for a new ingredient (create a new ingredient)
     *
     * @param ingredient
     *            the ingredient to add
     * @return Response Entity
     */
    @PostMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity createIngredient ( @RequestBody final Ingredient ingredient ) {
        if ( null != service.findByName( ingredient.getType() ) ) {
            return new ResponseEntity(
                    errorResponse( "Ingredient with the name " + ingredient.getType() + " already exists" ),
                    HttpStatus.CONFLICT );
        }

        if ( ingredient.getAmount() < 0 ) {
            return new ResponseEntity( errorResponse( "amount cannot be less than 0" ), HttpStatus.CONFLICT );
        }
        service.save( ingredient );
        return new ResponseEntity( successResponse( ingredient.getType() + " successfully created" ), HttpStatus.OK );
    }

    /**
     * PUT - updates a new ingredient takes in an ingredient object with the
     * same name as the one trying to update adds the amount from the new
     * ingredient to the one currently in the database
     *
     * @param ingredient
     *            the ingredient to add
     * @return Response Entity
     */
    @PutMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity updateIngredient ( @RequestBody final Ingredient ingredient ) {
        final Ingredient i = service.findByName( ingredient.getType() );
        if ( null == i ) {
            return new ResponseEntity(
                    errorResponse( "Ingredient with the name " + ingredient.getType() + " does not exist" ),
                    HttpStatus.CONFLICT );
        }

        if ( ingredient.getAmount() < 0 ) {
            return new ResponseEntity( errorResponse( "amount cannot be less than 0" ), HttpStatus.CONFLICT );
        }
        i.setAmount( i.getAmount() + ingredient.getAmount() );
        service.save( i );
        return new ResponseEntity( successResponse( ingredient.getType() + " was successfully updated" ),
                HttpStatus.OK );
    }

}
