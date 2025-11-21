package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.CartItemBulkDeleteRequest;
import com.example.shop_backend.dto.request.CartItemRequest;
import com.example.shop_backend.dto.response.CartItemResponse;
import com.example.shop_backend.dto.response.CartResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.CartMapper;
import com.example.shop_backend.model.*;
import com.example.shop_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartMapper cartMapper;

    // Thêm sản phẩm vào giỏ
    @Transactional
    public CartResponse addToCart(CartItemRequest request, User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });

        ProductVariant variant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Tìm xem item này đã có trong giỏ chưa
        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getProductVariant().getId().equals(variant.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            // existingItem.setItemTotalPrice(calcItemTotal(variant, existingItem.getQuantity()));
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(variant)
                    .quantity(request.getQuantity())
                    //.itemTotalPrice(calcItemTotal(variant, request.getQuantity()))
                    .build();
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    // Cập nhật số lượng
    @Transactional
    public CartResponse updateCartItem(Integer cartItemId, Integer quantity, User user) {
        Cart cart = getUserCart(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            //item.setItemTotalPrice(calcItemTotal(item.getProductVariant(), quantity));
        }

        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    // Xóa 1 item
    @Transactional
    public CartResponse removeCartItem(Integer cartItemId, User user) {
        Cart cart = getUserCart(user);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return buildCartResponse(cart);
    }

    // Xóa toàn bộ giỏ hàng
    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // Lấy giỏ hàng
    public CartResponse getCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeSelectedItems(CartItemBulkDeleteRequest request, User user) {

        if (user == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (request.getCartItemIds() == null || request.getCartItemIds().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        Cart cart = getUserCart(user);

        // Lọc ra các cartItem hợp lệ (thuộc về user)
        List<CartItem> itemsToRemove = cart.getItems().stream()
                .filter(item -> request.getCartItemIds().contains(item.getId()))
                .collect(Collectors.toList());

        if (itemsToRemove.isEmpty()) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // Xóa
        cart.getItems().removeAll(itemsToRemove);
        cartItemRepository.deleteAll(itemsToRemove);

        return buildCartResponse(cart);
    }


    // ===================== Helper =====================

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> {
                    BigDecimal price = item.getProductVariant().getProduct().getDiscountPrice() != null
                            ? item.getProductVariant().getProduct().getDiscountPrice()
                            : item.getProductVariant().getProduct().getPrice() != null
                            ? item.getProductVariant().getProduct().getPrice()
                            : BigDecimal.ZERO;

                    BigDecimal total = price.multiply(BigDecimal.valueOf(item.getQuantity()));

                    CartItemResponse resp = cartMapper.toCartItemResponse(item);
                    resp.setItemTotalPrice(total);
                    return resp;
                })
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemResponse::getItemTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .totalAmount(total)
                .build();
    }


    private BigDecimal calcItemTotal(ProductVariant variant, int quantity) {
        BigDecimal price = variant.getProduct().getDiscountPrice() != null
                ? variant.getProduct().getDiscountPrice()
                : variant.getProduct().getPrice();
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    private Cart getUserCart(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    }
}
