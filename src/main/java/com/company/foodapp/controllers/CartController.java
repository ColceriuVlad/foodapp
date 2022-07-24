package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Cart;
import com.company.foodapp.models.ErrorResponse;
import com.company.foodapp.models.Ordering;
import com.company.foodapp.repositories.CartRepository;
import com.company.foodapp.services.AuthorizationService;
import com.company.foodapp.services.CartService;
import com.company.foodapp.services.FoodService;
import com.company.foodapp.utils.DateUtils;
import com.company.foodapp.validators.OrderingValidator;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("cart")
@RestController
public class CartController {
    private CartRepository cartRepository;
    private CartService cartService;
    private Logger logger;
    private AuthorizationService authorizationService;
    private DateUtils dateUtils;
    private FoodService foodService;
    private OrderingValidator orderingValidator;

    @Autowired
    public CartController(CartRepository cartRepository, CartService cartService, Logger logger, AuthorizationService authorizationService, DateUtils dateUtils, FoodService foodService, OrderingValidator orderingValidator) {
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.logger = logger;
        this.authorizationService = authorizationService;
        this.dateUtils = dateUtils;
        this.foodService = foodService;
        this.orderingValidator = orderingValidator;
    }

    @GetMapping
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<List<Cart>> getAllCarts() {
        var carts = cartRepository.findAll();

        if (!carts.isEmpty()) {
            logger.info("Successfully retrieved all carts");
            return new ResponseEntity<>(carts, HttpStatus.OK);
        } else {
            logger.info("Could not retrieve any carts from the application");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{username}")
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<Cart> getCartByUserName(@PathVariable String username) {
        var cart = cartService.getCartByUserName(username);

        logger.info("Successfully retrieved the cart of user " + username);
        return new ResponseEntity(cart, HttpStatus.OK);
    }


    @PutMapping("{supplierName}/{foodName}")
    public ResponseEntity addFoodToCart(@PathVariable String supplierName, @PathVariable String foodName, HttpServletRequest httpServletRequest) {
        var currentAuthenticationDetails = authorizationService.getCurrentAuthenticationDetails(httpServletRequest);

        var food = foodService.getFoodFromSupplier(supplierName, foodName);

        if (food != null) {
            logger.info("Successfully retrieved current user");
            var currentUsername = currentAuthenticationDetails.subject;

            var cart = cartService.getCartByUserName(currentUsername);

            logger.info("Successfully retrieved the cart of the current user");

            var foodListFromCart = cart.foodList;

            if (!foodListFromCart.isEmpty()) {
                var firstFoodFromCart = cart.foodList.get(0);
                var firstFoodFromCartSupplier = firstFoodFromCart.supplier;
                var firstFoodFromCartSupplierName = firstFoodFromCartSupplier.name;

                if (firstFoodFromCartSupplierName.equals(food.supplier.name)) {
                    cart.foodList.add(food);
                    cartRepository.save(cart);
                    logger.info("Successfully added food: " + food.name + " from supplier " + food.supplier + "to user " + currentUsername);

                    return new ResponseEntity(HttpStatus.OK);
                } else {
                    var errorMessage = "Cannot add food to cart from a different supplier";
                    logger.info(errorMessage);

                    var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), errorMessage, dateUtils.getCurrentDate());
                    return new ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED);
                }
            } else {
                cart.foodList.add(food);
                cartRepository.save(cart);
                logger.info("Successfully added food: " + food.name + " from supplier " + food.supplier + "to user " + currentUsername);
                return new ResponseEntity(HttpStatus.OK);
            }
        } else {
            var errorMessage = String.format("Could not find food %s from supplier %s", foodName, supplierName);
            var errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    errorMessage,
                    dateUtils.getCurrentDate());

            return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{foodName}")
    public ResponseEntity deleteFoodFromCart(@PathVariable String foodName, HttpServletRequest httpServletRequest) {
        var currentAuthenticationDetails = authorizationService.getCurrentAuthenticationDetails(httpServletRequest);

        var currentUsername = currentAuthenticationDetails.subject;
        var cart = cartService.getCartByUserName(currentUsername);

        var cartWithDeletedFood = cartService.getCartWithDeletedFood(cart, foodName);

        if (cartWithDeletedFood != null) {
            cartRepository.save(cartWithDeletedFood);

            logger.info("Successfully deleted food " + foodName + " from cart");

            return new ResponseEntity(HttpStatus.OK);
        } else {
            var errorMessage = "Could not delete food " + foodName + " from cart";
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage, dateUtils.getCurrentDate());

            return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteFoodListFromCart(HttpServletRequest httpServletRequest) {
        cartService.deleteFoodListFromCart(httpServletRequest);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("ordering")
    public ResponseEntity addOrdering(HttpServletRequest httpServletRequest) {
        var authenticationDetails = authorizationService.getCurrentAuthenticationDetails(httpServletRequest);
        var currentUsername = authenticationDetails.subject;

        var cart = cartService.getCartByUserName(currentUsername);

        var ordering = new Ordering();
        ordering.timeStamp = dateUtils.getCurrentDate();

        var cartFoodList = cart.foodList;

        if (!cartFoodList.isEmpty()) {
            logger.info("Successfully retrieved food list from cart");
            ordering.totalPrice = 0;

            for (var cartFood : cartFoodList) {
                ordering.totalPrice = ordering.totalPrice + cartFood.price;
            }

            orderingValidator.getValidatedOrdering(ordering);

            cart.orderingList.add(ordering);
            cartRepository.save(cart);

            logger.info("Successfully added ordering");

            return new ResponseEntity(HttpStatus.OK);
        } else {
            var errorMessage = "Cart food list is empty, could not place order";
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage, dateUtils.getCurrentDate());
            return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
