package com.cdac.entity;

<<<<<<< HEAD
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
=======
import lombok.*;

import java.io.Serializable;

>>>>>>> 227a33330fb1c33e1f876118f2850c3e6dc3a838
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
<<<<<<< HEAD
public class Product {
	
	@Id
	private Long Id;

    private String title;     // Product title
    private String source;    // amazon or flipkart
    private double cost;      // price
    private String link;      // product link
=======
public class Product implements Serializable {

    private String id; // product ID (Amazon/Flipkart)

    private String title;
    private String source; // Amazon / Flipkart
    private double cost;
    private String imageUrl;
    private String productUrl;
>>>>>>> 227a33330fb1c33e1f876118f2850c3e6dc3a838
}
