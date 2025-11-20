package com.example.shop_backend.mapper;

import com.example.shop_backend.dto.request.CreateOrderRequest;
import com.example.shop_backend.dto.response.OrderResponse;
import com.example.shop_backend.model.*;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Named("mapFirstImage")
    static String mapFirstImage(ProductVariant variant) {
        if (variant.getImages() == null || variant.getImages().isEmpty()) {
            return null;
        }
        return variant.getImages().get(0).getImageUrl();
    }

    // ================================
    // Order -> OrderResponse
    // payment sẽ được map tự động
    // rewardPoints phải set thủ công trong service
    // ================================
    @Mapping(target = "items", source = "items")
    @Mapping(target = "payment", source = "payment")
    OrderResponse toOrderResponse(Order order);

    List<OrderResponse> toOrderResponses(List<Order> orders);

    // ================================
    // Payment -> PaymentResponse
    // ================================
    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "createdAt", source = "createdAt")
    OrderResponse.PaymentResponse toPaymentResponse(Payment payment);

    // ================================
    // OrderItem -> OrderItemResponse
    // totalPrice tự tính = unitPrice * quantity
    // ================================
    @Mapping(target = "productVariantId", source = "productVariant.id")
    @Mapping(target = "productSku", source = "productVariant.product.sku")
    @Mapping(target = "productName", source = "productVariant.product.name")
    @Mapping(target = "color", source = "productVariant.color.name")
    @Mapping(target = "size", source = "productVariant.size.name")
    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "totalPrice",
            expression = "java(orderItem.getUnitPrice().multiply(new java.math.BigDecimal(orderItem.getQuantity())))")
    @Mapping(target = "imageUrl",
            source = "productVariant",
            qualifiedByName = "mapFirstImage")
    OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem);
    List<OrderResponse.OrderItemResponse> toOrderItemResponses(List<OrderItem> items);

    // ================================
    // CartItem -> OrderItem
    // unitPrice lấy từ product discountPrice hoặc price
    // totalPrice = unitPrice * quantity
    // ================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "productVariant", source = "productVariant")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unitPrice", expression =
            "java(cartItem.getProductVariant().getProduct().getDiscountPrice() != null ? cartItem.getProductVariant().getProduct().getDiscountPrice() : cartItem.getProductVariant().getProduct().getPrice())")
    @Mapping(target = "totalPrice", expression =
            "java((cartItem.getProductVariant().getProduct().getDiscountPrice() != null ? cartItem.getProductVariant().getProduct().getDiscountPrice() : cartItem.getProductVariant().getProduct().getPrice()).multiply(new java.math.BigDecimal(cartItem.getQuantity())))")
    OrderItem cartItemToOrderItem(CartItem cartItem);

    List<OrderItem> cartItemsToOrderItems(List<CartItem> cartItems);

    // ================================
    // BuyNowItem -> OrderItem
    // unitPrice và totalPrice tự tính
    // ================================
    default OrderItem buyNowItemToOrderItem(CreateOrderRequest.BuyNowItem buyNowItem, ProductVariant variant) {
        BigDecimal unitPrice = variant.getProduct().getDiscountPrice() != null
                ? variant.getProduct().getDiscountPrice()
                : variant.getProduct().getPrice();

        OrderItem item = new OrderItem();
        item.setProductVariant(variant);
        item.setQuantity(buyNowItem.getQuantity());
        item.setUnitPrice(unitPrice);
        item.setTotalPrice(unitPrice.multiply(new BigDecimal(buyNowItem.getQuantity())));
        return item;
    }
}