package com.fse.shoppingapp.payload.response;

import org.bson.types.ObjectId;

import com.fse.shoppingapp.models.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {
	
	private ObjectId _id;	
	private String zipCode;
	private String loginId;
	private Integer quantity;
	private Product product;

}
