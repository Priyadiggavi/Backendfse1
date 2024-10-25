package com.fse.shoppingapp.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "wishlist")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wishlist {
	@Id
    private ObjectId _id;
    private String loginId;
    private int product_id;
    private String productName;
    private Integer quantity;

}
