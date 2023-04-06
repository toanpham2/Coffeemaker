package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.CoffeeOrder;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderRepository;

/**
 * Order service to keep track of all Orders.
 *
 * @author tgpham
 *
 */
@Component
@Transactional
public class OrderService extends Service<CoffeeOrder, Long> {
    /**
     * IngredientRepository, to be autowired in by Spring and provide CRUD
     * operations on Ingredient model.
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Gets the order repository
     */
    @Override
    protected JpaRepository<CoffeeOrder, Long> getRepository () {
        return orderRepository;
    }

    /**
     * finds the order by name
     *
     * @param name
     *            the name to add
     * @return the order object
     */
    public CoffeeOrder findByName ( final String name ) {
        return orderRepository.findByName( name );
    }
}
