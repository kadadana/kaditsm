package com.kadadana.kaditsm.modules.ticketcategory.controller;

import com.kadadana.kaditsm.core.model.ApiResponse;
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryCreateDTO;
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryResponseDTO;
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryUpdateDTO;
import com.kadadana.kaditsm.modules.ticketcategory.service.TicketCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class TicketCategoryController {

    private final TicketCategoryService categoryService;

    @GetMapping
    public ApiResponse<List<TicketCategoryResponseDTO>> getAllCategories() {
        List<TicketCategoryResponseDTO> categories = categoryService.getAllActiveCategories();
        return ApiResponse.success(categories, "Categories retrieved successfully.");
    }

    @PostMapping
    public ApiResponse<TicketCategoryResponseDTO> createCategory(@RequestBody TicketCategoryCreateDTO createDTO) {
        TicketCategoryResponseDTO createdCategory = categoryService.createCategory(createDTO);
        return ApiResponse.success(createdCategory, "Category created successfully.");
    }

    @PatchMapping("/{id}")
    public ApiResponse<TicketCategoryResponseDTO> patchCategory(
            @PathVariable UUID id,
            @RequestBody TicketCategoryUpdateDTO updateDTO) {
        TicketCategoryResponseDTO updatedCategory = categoryService.updateCategory(id, updateDTO);
        return ApiResponse.success(updatedCategory, "Category updated successfully.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deactivateCategory(id);
        return ApiResponse.success(null, "Category deactivated successfully.");
    }
}