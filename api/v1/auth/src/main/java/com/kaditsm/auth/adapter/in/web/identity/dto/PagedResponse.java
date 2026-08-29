package com.kaditsm.auth.adapter.in.web.identity.dto;

import java.util.List;

public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

}
