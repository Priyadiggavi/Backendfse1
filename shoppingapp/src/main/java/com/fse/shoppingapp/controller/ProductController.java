package com.fse.shoppingapp.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.fse.shoppingapp.exception.ProductsNotFound;
import com.fse.shoppingapp.exception.UserNotFound;
import com.fse.shoppingapp.models.Cart;
import com.fse.shoppingapp.models.Product;
import com.fse.shoppingapp.models.User;
import com.fse.shoppingapp.models.Wishlist;
import com.fse.shoppingapp.payload.request.LoginRequest;
import com.fse.shoppingapp.payload.response.CartResponseDTO;
import com.fse.shoppingapp.payload.response.MessageResponse;
import com.fse.shoppingapp.payload.response.WishListResponseDTO;
import com.fse.shoppingapp.repository.ProductRepository;
import com.fse.shoppingapp.repository.UserRepository;
import com.fse.shoppingapp.security.services.ProductService;
import com.fse.shoppingapp.security.services.SequenceGeneratorService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1.0/shopping")
@OpenAPIDefinition(info = @Info(title = "Shopping  Application API", description = "This API provides endpoints for managing Products."))
@Slf4j
public class ProductController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SequenceGeneratorService prodSequence;

	@PutMapping("/{loginId}/forgot")
	@Operation(summary = "reset password")
	public ResponseEntity<?> changePassword(@RequestBody LoginRequest loginRequest, @PathVariable String loginId) {
		log.debug("forgot password endopoint accessed by " + loginRequest.getLoginId());
		if (loginId.equals(loginRequest.getLoginId())) {
			Optional<User> user1 = userRepository.findByLoginId(loginId);
			if (user1 != null) {
				User availableUser = user1.get();
				User updatedUser = new User(loginId, availableUser.getFirstName(), availableUser.getLastName(),
						availableUser.getEmail(), availableUser.getContactNumber(),
						passwordEncoder.encode(loginRequest.getPassword()));
				updatedUser.set_id(availableUser.get_id());
				updatedUser.setRoles(availableUser.getRoles());
				userRepository.save(updatedUser);
				log.debug(loginRequest.getLoginId() + " has password changed successfully");
				return ResponseEntity
						.ok(new MessageResponse(loginRequest.getLoginId() + " has password changed successfully"));
			} else {
				throw new UserNotFound("LoginId Not Available");
			}
		} else {
			log.debug("Enter Correct LoginId");
			throw new UserNotFound("User Not Available");

		}

	}

	@GetMapping("/all")
	@Operation(summary = "search all Products")
	public List<Product> getAllProducts() {
		log.debug("here u can access all the available products");
		return productService.getAllProducts();

	}
	
	@GetMapping("/welcome")
	@Operation(summary = "Greetings for Azure")
	public String wish() {
		return "Welcome to Azure";

	}

	@GetMapping("/products/search/{productName}")
	@Operation(summary = "search product by product name")
	public Product getProductByName(@PathVariable String productName) {
		log.debug("here search a Product by its name");
		Product product = productService.getProductByName(productName);
		return product;

	}

	@PostMapping("/{productName}/add")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Add Product")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> addProducts(@RequestBody Product product, @PathVariable String productName) {
		if (productName.equals(product.getProductName())) {
			Product productDetails = productRepository.findByProductName(productName);
			if (productDetails == null) {
				product.setId(prodSequence.getSequenceNumber(Product.PRODUCT_SEQUENCE));
				productService.saveProduct(product);
				return new ResponseEntity<>("\"Added new Product\"", HttpStatus.OK);
			} else {
				Product updateProduct = new Product(productDetails.getId(), productDetails.getProductName(),
						productDetails.getProductDescription(), productDetails.getPrice(), productDetails.getFeatures(),
						productDetails.getQuantity(), productDetails.getProductStatus());
				updateProduct.setId(productDetails.getId());
				updateProduct.setQuantity(productDetails.getQuantity() + product.getQuantity());
				productService.saveProduct(updateProduct);
				return new ResponseEntity<>("\"Product Already Present!! Updated Quantity\"", HttpStatus.OK);
			}

		} else {
			return new ResponseEntity<>("Enter Correct ProductName!!!! ", HttpStatus.BAD_REQUEST);

		}

	}

	@PutMapping("/{productName}/update/{id}")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Update a ProductStatus(Admin Only)")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> updateProductStatus(@RequestBody Product product, @PathVariable Integer id) {

		Product productDetails = productService.getProductById(id);
		if (productDetails == null) {
			throw new ProductsNotFound("Products not found: " + product.getProductName());
		}
		Product updateProduct = new Product(productDetails.getId(), productDetails.getProductName(),
				productDetails.getProductDescription(), productDetails.getPrice(), productDetails.getFeatures(),
				productDetails.getQuantity(), productDetails.getProductStatus());
		updateProduct.setId(productDetails.getId());
		updateProduct.setProductName(product.getProductName());
		updateProduct.setProductDescription(product.getProductDescription());
		updateProduct.setPrice(product.getPrice());
		updateProduct.setFeatures(product.getFeatures());
		updateProduct.setQuantity(product.getQuantity());
		updateProduct.setProductStatus(product.getProductStatus());
		productService.saveProduct(updateProduct);
		return new ResponseEntity<>("\"Product Updated Successfully\"", HttpStatus.OK);
	}

	@DeleteMapping("/{productName}/delete/{id}")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "delete a Product(Admin Only)")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteProduct(@PathVariable String productName, @PathVariable int id) {
		Product availableProducts = productService.getProductByName(productName);
		if (availableProducts == null) {
			throw new ProductsNotFound("Products not found: " + productName);
		} else {
			productService.deleteByProductName(productName);
			return new ResponseEntity<>("\"Product Deleted Successfully!!\"", HttpStatus.OK);
		}

	}

//	/***** Cart Controllers ********/

	@PostMapping("/addToCart")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Add Product To Cart")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> addToCart(@RequestBody Cart product) {
		Product AvProduct = productService.getProductByName(product.getProductName());
		if (AvProduct != null && AvProduct.getQuantity() > 0) {
			Cart cartItem = productService.getAvailableProduct(product.getProductName());
			if (cartItem != null) {
				Cart updateCartProduct = new Cart(cartItem.get_id(), cartItem.getLoginId(), cartItem.getProduct_id(),
						cartItem.getProductName(), cartItem.getQuantity(), cartItem.getZipCode());
				updateCartProduct.set_id(cartItem.get_id());
				updateCartProduct.setQuantity(cartItem.getQuantity() + product.getQuantity());
				updateCartProduct.setZipCode(product.getZipCode());
				productService.addToCart(updateCartProduct);
				return new ResponseEntity<>("\"Product Already Present Updated quantity\"", HttpStatus.OK);
			} else {
				productService.addToCart(product);
				return new ResponseEntity<>("\"Product Added to Cart successfully\"", HttpStatus.OK);
			}

		} else {
			return new ResponseEntity<>("Product Not Available", HttpStatus.OK);
		}
	}

	@GetMapping("/getCart/{loginId}")
	@SecurityRequirement(name = "Bearer Authentication")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "GetCart Items For Specific User")
	public ResponseEntity<List<CartResponseDTO>> getCartByUser(@PathVariable String loginId) {
		List<CartResponseDTO> cartList = productService.getCartList(loginId);
		return new ResponseEntity<>(cartList, HttpStatus.OK);
	}

	@DeleteMapping("/removeProductFromCart/{productName}")
	@SecurityRequirement(name = "Bearer Authentication")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Delete items from cart")
	public ResponseEntity<String> removeProductFromCart(@PathVariable String productName) {
		log.info("Remove Product from cart from Controller method");
		productService.removeProductFromCart(productName);
		log.info("Remove product from cart service executed successfuly");
		return new ResponseEntity<>("\"Deleted successfully\"", HttpStatus.OK);

	}

//	/********** WishList Controllers ***********/

	@PostMapping("/addToWishList")
	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Add Product To WishList")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> addToWishList(@RequestBody Wishlist product) {
		Product AvProduct = productService.getProductByName(product.getProductName());
		if (AvProduct != null) {
			Wishlist wishListItem = productService.getWishList(product.getProductName());
			if (wishListItem != null) {
				return new ResponseEntity<>("\"Product Already Added!!\"", HttpStatus.OK);
			} else {
				productService.addToWishList(product);
				return new ResponseEntity<>("\"Product Added to Wishlist successfully\"", HttpStatus.OK);
			}
		}
		return null;
	}
	
	@GetMapping("/getWishList/{loginId}")
	@SecurityRequirement(name = "Bearer Authentication")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "GetCart Items For Specific User")
	public ResponseEntity<List<WishListResponseDTO>> getWishListByUser(@PathVariable String loginId) {
		List<WishListResponseDTO> wishListData = productService.getWishListData(loginId);
		return new ResponseEntity<>(wishListData, HttpStatus.OK);
	}
	
	@DeleteMapping("/removeProductFromWishList/{productName}")
	@SecurityRequirement(name = "Bearer Authentication")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Delete items from WishList")
	public ResponseEntity<String> removeProductFromWishList(@PathVariable String productName) {
		log.info("Remove Product from wishList from Controller method");
		productService.removeProductFromWishList(productName);
		log.info("Remove product from wishList service executed successfuly");
		return new ResponseEntity<>("\"Deleted successfully\"", HttpStatus.OK);

	}

}
