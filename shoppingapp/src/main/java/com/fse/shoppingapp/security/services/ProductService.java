package com.fse.shoppingapp.security.services;

import com.fse.shoppingapp.exception.ProductsNotFound;
import com.fse.shoppingapp.models.Cart;
import com.fse.shoppingapp.models.Product;
import com.fse.shoppingapp.models.Wishlist;
import com.fse.shoppingapp.payload.response.CartResponseDTO;
import com.fse.shoppingapp.payload.response.MessageResponse;
import com.fse.shoppingapp.payload.response.WishListResponseDTO;
import com.fse.shoppingapp.repository.CartRepository;
import com.fse.shoppingapp.repository.ProductRepository;
import com.fse.shoppingapp.repository.WishListRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private WishListRepository wishListRepository;

	public List<Product> getAllProducts() {
		List<Product> productList = productRepository.findAll();
		if (productList.isEmpty()) {
			log.debug("currently no products are available");
			throw new ProductsNotFound("No products are available");
		} else {
			log.debug("listed the available products");
			return productList;
		}
	}

	// Cart Services
	public void addToCart(Cart cart) {
		cartRepository.save(cart);
	}

	public Cart getAvailableProduct(String productName) {
		return cartRepository.findByProductName(productName);
	}

	public List<CartResponseDTO> getCartList(String loginId) {
		List<CartResponseDTO> cartDTOList = new ArrayList<>();
		List<Cart> cartList = cartRepository.findByLoginId(loginId);
		for (Cart cart : cartList) {
			Product product = productRepository.findByProductName(cart.getProductName());
			if (product != null) {
				cartDTOList.add(new CartResponseDTO(cart.get_id(), cart.getZipCode(), cart.getLoginId(),
						cart.getQuantity(), product));
			}else {
				cartRepository.deleteByProductName(cart.getProductName());
			}

		}
		return cartDTOList;
	}

	public void saveProduct(Product product) {
		productRepository.save(product);
	}

	public Product getProductByName(String productName) {
		Product productList = productRepository.findByProductName(productName);
		if (productList == null) {
			throw new ProductsNotFound("Products Not Found");
		} else {
			return productList;
		}

	}

	public Product getProductById(Integer productId) {
		Optional<Product> productList = productRepository.findById(productId);
		if (productList == null) {
			throw new ProductsNotFound("Products Not Found");
		} else {
			Product productUpdate = productList.get();
			return productUpdate;
		}

	}

	public ResponseEntity<?> deleteByProductName(String productName) {
		productRepository.deleteByProductName(productName);
		return ResponseEntity.ok("Product deleted successfully");
	}

	public ResponseEntity<?> removeProductFromCart(String productName) {
		cartRepository.deleteByProductName(productName);
		return ResponseEntity.ok("Deleted successfully");
	}
	
//	/*************WishList Services ****************/
	
	public void addToWishList(Wishlist wishListData) {
		wishListRepository.save(wishListData);
	}

	public Wishlist getWishList(String productName) {
		return wishListRepository.findByProductName(productName);
	}
	
	public List<WishListResponseDTO> getWishListData(String loginId) {
		List<WishListResponseDTO> wishListDTO = new ArrayList<>();
		List<Wishlist> wishList = wishListRepository.findByLoginId(loginId);
		for (Wishlist wish : wishList) {
			Product product = productRepository.findByProductName(wish.getProductName());
			if (product != null) {
				wishListDTO.add(new WishListResponseDTO(wish.get_id(), wish.getLoginId(),
						wish.getQuantity(), product));
			}else {
				wishListRepository.deleteByProductName(wish.getProductName());
			}

		}
		return wishListDTO;
	}
	
	public ResponseEntity<?> removeProductFromWishList(String productName) {
		wishListRepository.deleteByProductName(productName);
		return ResponseEntity.ok("Deleted successfully");
	}
	

}
