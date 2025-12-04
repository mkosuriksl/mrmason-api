package com.application.mrmason.dto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PaginatedResponse<T> implements Serializable {

    private List<T> items = new ArrayList<>();
    private Integer pageSize = 0;
    private Integer pageNumber = 0;
    private Long totalElements = 0L;
    private Integer totalPages = 0;
}