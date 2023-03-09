package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;

/**
 * IngredientRepository is used to provide CRUD operations for the Ingredient
 * model. Spring will generate appropriate code with JPA.
 *
 */
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * finds the ingredient by name
     *
     * @param name
     *            the name of the ingredient to find
     * @return the ingredient object
     */
    Ingredient findByName ( String name );

}
