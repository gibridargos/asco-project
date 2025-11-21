package com.ascoproject.ascoproject.service;

import com.ascoproject.ascoproject.entity.Role;
import com.ascoproject.ascoproject.entity.User;
import com.ascoproject.ascoproject.model.auth.AuthenticationRequest;
import com.ascoproject.ascoproject.model.auth.AuthenticationResponse;
import com.ascoproject.ascoproject.model.auth.RegisterRequest;
import com.ascoproject.ascoproject.model.responce.ResponseAll;
import com.ascoproject.ascoproject.model.responce.ResponseResult;
import com.ascoproject.ascoproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public ResponseAll<ResponseResult<AuthenticationResponse>> register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        ResponseResult<AuthenticationResponse> result = new ResponseResult<>();
        result.setResult(AuthenticationResponse.builder().token(jwt).build());
        return ResponseAll.<ResponseResult<AuthenticationResponse>>builder()
                .response(result)
                .status(200)
                .build();
    }


    public ResponseAll<ResponseResult<AuthenticationResponse>> authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        var user = userRepository.findUserByUsername(request.getUsername()).orElseThrow();
        var jwt = jwtService.generateToken(user);
        ResponseResult<AuthenticationResponse> result = new ResponseResult<>();
        result.setResult(AuthenticationResponse.builder().token(jwt).build());
        return ResponseAll.<ResponseResult<AuthenticationResponse>>builder()
                .response(result)
                .status(200)
                .build();
    }
}
