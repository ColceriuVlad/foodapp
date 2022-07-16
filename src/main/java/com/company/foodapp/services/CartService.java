package com.company.foodapp.services;

import com.company.foodapp.models.Cart;
import com.company.foodapp.models.Food;
import com.company.foodapp.models.User;
import com.company.foodapp.repositories.CartRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private CartRepository cartRepository;
    private Logger logger;

    @Autowired
    public CartService(CartRepository cartRepository, Logger logger) {
        this.cartRepository = cartRepository;
        this.logger = logger;
    }

    public boolean addCartToUser(User user) {
        try {
            var cart = new Cart();
            cart.user = user;
            cartRepository.save(cart);

            logger.info("Cart was successfully added to user " + user.username);
            return true;
        } catch (Exception exception) {
            logger.info("Could not add cart to user " + user.username);
            return false;
        }
    }

    public Cart getCartByUserName(String username) {
        var carts = cartRepository.findAll();

        if (!carts.isEmpty()) {
            for (var cart : carts) {
                if (cart.user.username.equals(username)) {
                    logger.info("Successfully retrieved cart for user " + username);
                    return cart;
                }
            }

            logger.info("Could not retrieve the cart of user " + username);
            return null;
        } else {
            logger.info("Could not retrieve any carts from the application");
            return null;
        }
    }

    public Cart getCartWithDeletedFood(Cart cart, String foodName) {
        var foodList = cart.foodList;

        if (foodList != null) {
            for (var i = 0; i < cart.foodList.size(); i++) {
                if (foodList.get(i).name.equals(foodName)) {
                    cart.foodList.remove(i);
                    logger.info("Successfully removed food " + foodName + "from cart");

                    return cart;
                }
            }
            logger.info("Food " + foodName + "was not found in the current cart");
            return null;
        } else {
            logger.info("There is no food added in the current cart");
            return null;
        }
    }
}
