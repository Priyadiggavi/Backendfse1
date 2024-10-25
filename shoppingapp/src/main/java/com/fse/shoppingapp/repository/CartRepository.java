package com.fse.shoppingapp.repository;

import com.fse.shoppingapp.models.Cart;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartRepository extends MongoRepository<Cart,String> {

    Cart findByProductName(String productName);

    List<Cart> findBy_id(ObjectId _id);
    
    List<Cart> findByLoginId(String loginId);
    
    void deleteByProductName(String productName);
}
