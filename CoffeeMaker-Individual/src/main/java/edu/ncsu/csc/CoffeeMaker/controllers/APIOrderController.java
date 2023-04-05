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

import edu.ncsu.csc.CoffeeMaker.models.CoffeeOrder;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;

@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /** the service for orders */
    @Autowired
    private OrderService     service;

    /** the service for orders */
    @Autowired
    private InventoryService inventoryService;

    /**
     * gets an order from the database
     *
     * @param name
     *            the name of the order to get
     * @return response entity
     */
    @GetMapping ( BASE_PATH + "/orders/{name}" )
    public ResponseEntity getOrder ( @PathVariable final String name ) {

        final CoffeeOrder order = service.findByName( name );

        if ( null == order ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( order, HttpStatus.OK );
    }

    /**
     * REST API method to provide GET access to all orders in the system
     *
     * @return JSON representation of all orders
     */
    @GetMapping ( BASE_PATH + "/orders" )
    public List<CoffeeOrder> getOrders () {
        return service.findAll();
    }

    /**
     * DELETE - deletes a specified order from the database
     *
     * @param id
     *            the id to delete the name of the ingredient to delete
     * @return Response Entity
     */
    @DeleteMapping ( BASE_PATH + "/orders/{id}" )
    public ResponseEntity deleteOrder ( @PathVariable final Long id ) {
        System.out.println( "Deleting id " + id.toString() );
        final CoffeeOrder order = service.findById( id );
        if ( null == order ) {
            return new ResponseEntity( errorResponse( "No order found for name " + order.getName() ),
                    HttpStatus.NOT_FOUND );
        }
        service.delete( order );

        return new ResponseEntity( successResponse( order.getName() + " was deleted successfully" ), HttpStatus.OK );
    }

    /**
     * POST for a new order (create a new ordedr)
     *
     * @param order
     *            the order to add
     * @return Response Entity
     */
    @PostMapping ( BASE_PATH + "/orders" )
    public ResponseEntity createOrder ( @RequestBody final CoffeeOrder order ) {
        if ( null != service.findByName( order.getName() ) ) {
            return new ResponseEntity( errorResponse( "Order with the name " + order.getName() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        service.save( order );
        return new ResponseEntity( successResponse( order.getName() + " successfully created" ), HttpStatus.OK );
    }

    /**
     * PUT - updates an order's isFulfilled field to true
     *
     * @param order
     *            the order to edit
     * @return Response Entity
     */
    @PutMapping ( BASE_PATH + "/ordersFulfilled" )
    public ResponseEntity updateOrdersFulfilled ( @RequestBody final CoffeeOrder order ) {
        final CoffeeOrder o = service.findByName( order.getName() );
        final Inventory inventory = inventoryService.getInventory();
        if ( null == o ) {
            return new ResponseEntity( errorResponse( "Order with the name " + order.getName() + " does not exist" ),
                    HttpStatus.NOT_FOUND );
        }
        for ( int i = 0; i < o.getRecipe().getAllIngredients().size(); i++ ) {
            if ( o.getRecipe().getAllIngredients().get( i ).getAmount() < inventory
                    .getOneIngredient( o.getRecipe().getAllIngredients().get( i ).getType() ).getAmount() ) {
                return new ResponseEntity( errorResponse( "does not have enough Inventory" ), HttpStatus.CONFLICT );
            }

        }
        o.setFulfilled( true );
        service.save( o );
        return new ResponseEntity( successResponse( order.getName() + " was successfully updated to fulfilled" ),
                HttpStatus.OK );
    }

    /**
     * PUT - updates an order's isPickedUp field to true
     *
     * @param order
     *            the order to edit
     * @return Response Entity
     */
    @PutMapping ( BASE_PATH + "/ordersPickedUp" )
    public ResponseEntity updateOrdersPickedUp ( @RequestBody final CoffeeOrder order ) {
        final CoffeeOrder o = service.findByName( order.getName() );
        if ( null == o ) {
            return new ResponseEntity( errorResponse( "Order with the name " + order.getName() + " does not exist" ),
                    HttpStatus.NOT_FOUND );
        }

        o.setPickedUp( true );
        service.save( o );
        return new ResponseEntity( successResponse( order.getName() + " was successfully updated to picked up" ),
                HttpStatus.OK );
    }

}
