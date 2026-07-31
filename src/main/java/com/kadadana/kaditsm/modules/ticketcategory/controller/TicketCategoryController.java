package com.kadadana.kaditsm.modules.ticketcategory.controller;

//JAVA IMPORTS
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.UUID;

//CORE IMPORTS
import com.kadadana.kaditsm.core.model.ApiResponse;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryCreateDTO;
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryResponseDTO;
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryUpdateDTO;
import com.kadadana.kaditsm.modules.ticketcategory.service.TicketCategoryService;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints to manage ticket categories")
public class TicketCategoryController {

    private final TicketCategoryService categoryService;

    @GetMapping
    @Operation(summary = "List categories", description = "Retrieve a list of active ticket categories.")
    public ApiResponse<List<TicketCategoryResponseDTO>> getAllCategories() {
        List<TicketCategoryResponseDTO> categories = categoryService.getAllActiveCategories();
        return ApiResponse.success(categories, "Categories retrieved successfully.");
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new ticket category.")
    public ApiResponse<TicketCategoryResponseDTO> createCategory(@RequestBody TicketCategoryCreateDTO createDTO) {
        TicketCategoryResponseDTO createdCategory = categoryService.createCategory(createDTO);
        return ApiResponse.success(createdCategory, "Category created successfully.");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing ticket category by ID.")
    public ApiResponse<TicketCategoryResponseDTO> patchCategory(
            @PathVariable UUID id,
            @RequestBody TicketCategoryUpdateDTO updateDTO) {
        TicketCategoryResponseDTO updatedCategory = categoryService.updateCategory(id, updateDTO);
        return ApiResponse.success(updatedCategory, "Category updated successfully.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate category", description = "Deactivate a ticket category instead of hard deleting it.")
    public ApiResponse<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deactivateCategory(id);
        return ApiResponse.success(null, "Category deactivated successfully.");
    }
}