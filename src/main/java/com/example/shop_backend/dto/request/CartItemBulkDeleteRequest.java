package com.example.shop_backend.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CartItemBulkDeleteRequest {
    private List<Integer> cartItemIds;
}
