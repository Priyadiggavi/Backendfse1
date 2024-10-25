package com.fse.shoppingapp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fse.shoppingapp.models.ERole;
import com.fse.shoppingapp.models.Role;
import com.fse.shoppingapp.models.User;
import com.fse.shoppingapp.repository.RoleRepository;
import com.fse.shoppingapp.repository.UserRepository;




@SpringBootApplication
public class ShoppingappApplication implements CommandLineRunner {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepo;

	
	@Autowired
    PasswordEncoder encoder;
	
	
	@Autowired
	private MongoTemplate mongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(ShoppingappApplication.class, args);
	}

	public void run(String... args) throws Exception {
		
		mongoTemplate.dropCollection("roles");
		mongoTemplate.dropCollection("users");
		Role admin = new Role(ERole.ROLE_ADMIN);
		Role user = new Role(ERole.ROLE_USER);
		roleRepository.saveAll(Arrays.asList(admin,user));
		
		User admin1= new User();
		admin1.setLoginId("Admin1");
		admin1.setFirstName("Padmapriya");
		admin1.setLastName("Diggavi");
		admin1.setEmail("priya123@gmail.com");
		admin1.setContactNumber(1234567890L);
		Set<Role> roles = new HashSet<>();
		String errorMessegae = "Error: Role is not found.";
		Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException(errorMessegae));
        roles.add(adminRole);
        admin1.setRoles(roles);
        admin1.setPassword(encoder.encode("admin@123"));
		userRepo.save(admin1);
	}

}
