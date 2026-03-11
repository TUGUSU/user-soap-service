package com.example.usersoapservice.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.example.auth.LoginUserRequest;
import com.example.auth.LoginUserResponse;
import com.example.auth.RegisterUserRequest;
import com.example.auth.RegisterUserResponse;
import com.example.auth.ValidateTokenRequest;
import com.example.auth.ValidateTokenResponse;

@Endpoint
public class AuthEndpoint {

	private static final String NAMESPACE_URI = "http://example.com/auth";

	// username -> password
	private final Map<String, String> users = new HashMap<>();

	// token -> username
	private final Map<String, String> tokens = new HashMap<>();

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "RegisterUserRequest")
	@ResponsePayload
	public RegisterUserResponse registerUser(@RequestPayload RegisterUserRequest request) {
		RegisterUserResponse response = new RegisterUserResponse();

		if (users.containsKey(request.getUsername())) {
			response.setSuccess(false);
			response.setMessage("Username already exists.");
		} else {
			users.put(request.getUsername(), request.getPassword());
			response.setSuccess(true);
			response.setMessage("User registered successfully.");
		}

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "LoginUserRequest")
	@ResponsePayload
	public LoginUserResponse loginUser(@RequestPayload LoginUserRequest request) {
		LoginUserResponse response = new LoginUserResponse();

		String storedPassword = users.get(request.getUsername());

		if (storedPassword != null && storedPassword.equals(request.getPassword())) {
			String token = UUID.randomUUID().toString();
			tokens.put(token, request.getUsername());

			response.setSuccess(true);
			response.setToken(token);
			response.setMessage("Login successful.");
		} else {
			response.setSuccess(false);
			response.setMessage("Invalid username or password.");
		}

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ValidateTokenRequest")
	@ResponsePayload
	public ValidateTokenResponse validateToken(@RequestPayload ValidateTokenRequest request) {
		ValidateTokenResponse response = new ValidateTokenResponse();

		String username = tokens.get(request.getToken());

		if (username != null) {
			response.setValid(true);
			response.setUsername(username);
		} else {
			response.setValid(false);
		}

		return response;
	}
}