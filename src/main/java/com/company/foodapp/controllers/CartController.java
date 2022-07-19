package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Cart;
import com.company.foodapp.models.ErrorResponse;
import com.company.foodapp.repositories.CartRepository;
import com.company.foodapp.services.AuthorizationService;
import com.company.foodapp.services.CartService;
import com.company.foodapp.services.FoodService;
import com.company.foodapp.utils.DateUtils;
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

    @Autowired
    public CartController(CartRepository cartRepository, CartService cartService, Logger logger, AuthorizationService authorizationService, DateUtils dateUtils, FoodService foodService) {
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.logger = logger;
        this.authorizationService = authorizationService;
        this.dateUtils = dateUtils;
        this.foodService = foodService;
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

        if (cart != null) {
            logger.info("Successfully retrieved the cart of user " + username);
            return new ResponseEntity(cart, HttpStatus.OK);
        } else {
            logger.info("Could not retrieve the cart of user " + username);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("{supplierName}/{foodName}")
    public ResponseEntity addFoodToCart(@PathVariable String supplierName, @PathVariable String foodName, HttpServletRequest httpServletRequest) {
        var currentAuthenticationDetails = authorizationService.getCurrentAuthenticationDetails(httpServletRequest);

        if (currentAuthenticationDetails != null) {
            var food = foodService.getFoodFromSupplier(supplierName, foodName);

            if (food != null) {
                logger.info("Successfully retrieved current user");
                var currentUsername = currentAuthenticationDetails.subject;

                var cart = cartService.getCartByUserName(currentUsername);

                if (cart != null) {
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
                    var errorMessage = "Could not retrieve the cart of the current user";
                    logger.info(errorMessage);

                    var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage, dateUtils.getCurrentDate());
                    return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
                }
            } else {
                var errorMessage = String.format("Could not find food %s from supplier %s", foodName, supplierName);
                var errorResponse = new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        errorMessage,
                        dateUtils.getCurrentDate());

                return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
            }
        } else {
            var errorMessage = "User is not logged in, cannot add food to cart";
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), errorMessage, dateUtils.getCurrentDate());
            return new ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("{foodName}")
    public ResponseEntity deleteFoodFromCart(@PathVariable String foodName, HttpServletRequest httpServletRequest) {
        var currentAuthenticationDetails = authorizationService.getCurrentAuthenticationDetails(httpServletRequest);

        if (currentAuthenticationDetails != null) {
            var currentUsername = currentAuthenticationDetails.subject;
            var cart = cartService.getCartByUserName(currentUsername);

            if (cart != null) {
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
            } else {
                var errorMessage = "Could not find the cart of the current user";
                logger.info(errorMessage);

                var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage, dateUtils.getCurrentDate());

                return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
            }
        } else {
            var errorMessage = "User is not logged in, cannot remove food from cart";
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), errorMessage, dateUtils.getCurrentDate());

            return new ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteFoodListFromCart(HttpServletRequest httpServletRequest) {
        var authenticationDetails = authorizationService.getCurrentAuthenticationDetails(httpServletRequest);

        if (authenticationDetails != null) {
            logger.info("Successfully retrieved authentication details");

            var currentUsername = authenticationDetails.subject;
            var cart = cartService.getCartByUserName(currentUsername);

            if (cart != null) {
                logger.info("Successfully retrieved the cart of the current user");

                if (!cart.foodList.isEmpty()) {
                    logger.info("Successfully found the cart foodlist");
                    cart.foodList = null;
                    cartRepository.save(cart);
                    logger.info("Successfully deleted the foodlist from cart");

                    return new ResponseEntity(HttpStatus.OK);
                } else {
                    var errorMessage = "Could not find the cart foodlist";
                    logger.info(errorMessage);

                    var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage, dateUtils.getCurrentDate());

                    return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
                }

            } else {
                var errorMessage = "Could not retrieve the cart of the current user";
                logger.info(errorMessage);

                var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage, dateUtils.getCurrentDate());

                return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
            }
        } else {
            var errorMessage = "Could not retrieve authentication details";
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), errorMessage, dateUtils.getCurrentDate());

            return new ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }
}
