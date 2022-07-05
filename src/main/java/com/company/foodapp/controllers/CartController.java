package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Cart;
import com.company.foodapp.repositories.CartRepository;
import com.company.foodapp.services.CartService;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("cart")
@RestController
public class CartController {
    private CartRepository cartRepository;
    private CartService cartService;
    private Logger logger;

    @Autowired
    public CartController(CartRepository cartRepository, CartService cartService, Logger logger) {
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.logger = logger;
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
}
