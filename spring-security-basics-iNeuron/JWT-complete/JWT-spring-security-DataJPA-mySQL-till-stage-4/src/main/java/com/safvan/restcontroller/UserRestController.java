package com.safvan.restcontroller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safvan.model.User;
import com.safvan.model.UserRequest;
import com.safvan.model.UserResponse;
import com.safvan.service.IUserService;
import com.safvan.util.JwtUtil;

@RestController
@RequestMapping("/user")
public class UserRestController {

	@Autowired
	private IUserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	// 1: save user data in database
	@PostMapping("/save")
	public ResponseEntity<String> saveUser(@RequestBody User user) {

		Integer userId = userService.saveUser(user);
		String body = "user with ID :" + userId + " saved..";
		System.out.println(body);
		return ResponseEntity.ok(body);
	}

	// 2: Validate user and generate Token
	@PostMapping("/login")
	public ResponseEntity<UserResponse> loginUser(@RequestBody UserRequest request) {

		// validate username and psswd with DB

		String username = request.getUsername();
		Serializable password = request.getPassword();
		// this method is required incase of STATELESS Auth.
		// Token generated only if user Authenticated.
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		String token = jwtUtil.generateToken(request.getUsername());

		return ResponseEntity.ok(new UserResponse(token, "Success, Generated By Safvan"));
	}
}