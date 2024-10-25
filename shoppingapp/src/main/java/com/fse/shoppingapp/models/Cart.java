package com.fse.shoppingapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
	@Id
    private ObjectId _id;
    private String loginId;
    private int product_id;
    private String productName;
    private Integer quantity;
    private String zipCode;
    
	public Cart(String loginId, String productName, Integer quantity, String zipCode) {
		super();
		this.loginId = loginId;
		this.productName = productName;
		this.quantity = quantity;
		this.zipCode = zipCode;
	}
}
