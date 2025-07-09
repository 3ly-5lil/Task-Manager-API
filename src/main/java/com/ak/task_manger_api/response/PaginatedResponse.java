package com.ak.task_manger_api.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PaginatedResponse<T> {
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final int totalPages;
    private final int numberOfElements;
    private final long totalElements;

    public PaginatedResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
        this.numberOfElements = page.getNumberOfElements();
        this.totalElements = page.getTotalElements();
    }
}
