package com.fse.shoppingapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	
	@Transient
	public static final String PRODUCT_SEQUENCE = "productSequence";
	
	@Id
    private int id;
	
	@NotBlank
    private String productName;
	
	@NotBlank
    private String productDescription;
	
	@NotBlank
    private Integer price;
	
	@NotBlank
    private String features;
	
	@NotBlank
    private Integer quantity;
	
	@NotBlank
    private String productStatus;

	public Product(@NotBlank String productName, @NotBlank String productDescription, @NotBlank Integer price,
			@NotBlank String features, @NotBlank Integer quantity, @NotBlank String productStatus) {
		super();
		this.productName = productName;
		this.productDescription = productDescription;
		this.price = price;
		this.features = features;
		this.quantity = quantity;
		this.productStatus = productStatus;
	}
	
	

}
