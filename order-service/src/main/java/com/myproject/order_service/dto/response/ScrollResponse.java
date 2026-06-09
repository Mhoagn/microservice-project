package com.myproject.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ScrollResponse<T> {

    private List<T> data;

    private Long nextCursor;
    private boolean hasNext;
}
