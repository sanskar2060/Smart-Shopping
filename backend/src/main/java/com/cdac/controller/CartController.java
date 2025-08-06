package com.cdac.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dto.AddProductToCartRequest;
import com.cdac.dto.RemoveProductFromCartRequest;
import com.cdac.entity.Product;
import com.cdac.service.CartService;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*") // allow cross-origin requests if frontend is separate
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addProductToCart(@RequestBody AddProductToCartRequest request) {
        cartService.addProductToCart(request);
        return    "Product added to cart successfully";
    }
    
/// GET endpoint to retrieve all products in a user's cart
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartProducts(@PathVariable Long userId) {
    	
   
        try {
            List<Product> products = cartService.getProductsByUserId(userId);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    
    // DELETE endpoint to remove a product from the cart
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeProductFromCart(@RequestBody RemoveProductFromCartRequest request) {
        try {
            cartService.removeProductFromCart(request);
            return new ResponseEntity<>("Product removed from cart successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
}