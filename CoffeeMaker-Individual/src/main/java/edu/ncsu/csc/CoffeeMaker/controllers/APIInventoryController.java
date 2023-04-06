package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * update operations for the Inventory.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIInventoryController extends APIController {

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService service;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * Inventory. This will convert the Inventory to JSON.
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity getInventory () {
        final Inventory inventory = service.getInventory();
        return new ResponseEntity( inventory, HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's singleton
     * Inventory. This will update the Inventory of the CoffeeMaker by adding
     * amounts from the Inventory provided to the CoffeeMaker's stored inventory
     *
     * @param name
     *            the name of the ingredient to put in the path
     * @param ingredient
     *            the ingredients to add amounts to add to inventory
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/inventory/{name}" )
    public ResponseEntity updateInventory ( @PathVariable final String name,
            @RequestBody final Ingredient ingredient ) {
        final Inventory inventoryCurrent = service.getInventory();
        if ( ingredient != null ) {
            try {
                if ( ingredient.getAmount() < 0 ) {
                    return new ResponseEntity( errorResponse( "Can't have negitive amount" ), HttpStatus.BAD_REQUEST );
                }
                inventoryCurrent.addIngredients( ingredient );
            }
            catch ( final IllegalArgumentException e ) {
                // inventoryCurrent.updateIngredients( ingredient );
                return new ResponseEntity( errorResponse( "Ingredient already exist" ), HttpStatus.BAD_REQUEST );
            }
        }

        service.save( inventoryCurrent );
        return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
    }

    /**
     * put for updating the inventory with additional ingredient amounts
     *
     * @param ingredients
     *            the list of ingredients with their values to add
     * @return Response Entity
     */
    @PutMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity updateInventoryIngredients ( @RequestBody final List<Ingredient> ingredients ) {
        final Inventory inventoryCurrent = service.getInventory();
        if ( ingredients != null ) {

            for ( final Ingredient i : ingredients ) {
                try {
                    inventoryCurrent.updateIngredients( i );
                }
                catch ( final IllegalArgumentException e ) {
                    return new ResponseEntity( errorResponse( "Can't have negitive amount" ), HttpStatus.BAD_REQUEST );
                }
            }

        }

        service.save( inventoryCurrent );
        return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
    }
}
