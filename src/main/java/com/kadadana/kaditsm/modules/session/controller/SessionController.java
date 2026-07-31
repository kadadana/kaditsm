package com.kadadana.kaditsm.modules.session.controller;

//JAVA IMPORTS
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

//CORE IMPORTS
import com.kadadana.kaditsm.core.model.ApiResponse;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.session.dto.LoginRequestDTO;
import com.kadadana.kaditsm.modules.session.service.SessionService;
import com.kadadana.kaditsm.modules.session.dto.SessionResponseDTO;

@RestController
@RequestMapping("/api/session")
@Tag(name = "Session", description = "Login and logout endpoints")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    @Operation(summary = "Login", description = "Authenticate user and create a session. Returns tokens on successful authentication.")
    public ApiResponse<SessionResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ApiResponse.success(sessionService.login(request), "Login successful.");
    }

    @DeleteMapping
    @Operation(summary = "Logout", description = "Invalidate the current JWT and end the session.")
    public ApiResponse<SessionResponseDTO> logout(@RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.replace("Bearer ", "");
        sessionService.logout(jwt);

        return ApiResponse.success(null, "Logout successful.");

    }
}