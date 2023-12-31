package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.CoffeeOrder;

/**
 * IngredientRepository is used to provide CRUD operations for the Ingredient
 * model. Spring will generate appropriate code with JPA.
 *
 */
public interface OrderRepository extends JpaRepository<CoffeeOrder, Long> {

    /**
     * finds the Order by name
     *
     * @param name
     *            the name of the ingredient to find
     * @return the order object
     */
    CoffeeOrder findByName ( String name );

}
