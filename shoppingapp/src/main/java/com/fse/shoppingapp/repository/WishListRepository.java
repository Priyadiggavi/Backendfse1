package com.fse.shoppingapp.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.fse.shoppingapp.models.Wishlist;

public interface WishListRepository extends MongoRepository<Wishlist, String> {
	
    Wishlist findByProductName(String productName);

    List<Wishlist> findBy_id(ObjectId _id);
    
    List<Wishlist> findByLoginId(String loginId);
    
    void deleteByProductName(String productName);

}
