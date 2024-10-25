package com.fse.shoppingapp.models;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "databaseSequence")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseSequence {
	
	
	private String Id;
	private Integer seq;
	
}
