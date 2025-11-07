package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.WishlistRequest;
import com.example.shop_backend.dto.response.WishlistResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.WishlistMapper;
import com.example.shop_backend.model.*;
import com.example.shop_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;

    /**
     * Thêm hoặc bỏ thích sản phẩm (toggle)
     */
    @Transactional
    public String toggleWishlist(WishlistRequest request, User user) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        var existing = wishlistRepository.findByUserAndProduct(user, product);
        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            return "Đã bỏ thích sản phẩm";
        } else {
            Wishlist wishlist = Wishlist.builder()
                    .user(user)
                    .product(product)
                    .build();
            wishlistRepository.save(wishlist);
            return "Đã thêm sản phẩm vào danh sách yêu thích";
        }
    }

    /**
     * Lấy danh sách sản phẩm yêu thích của người dùng
     */
    public List<WishlistResponse> getUserWishlist(User user) {
        List<Wishlist> wishlists = wishlistRepository.findAllByUser(user);
        return wishlists.stream().map(w -> {
            WishlistResponse res = wishlistMapper.toWishlistResponse(w);
            long likeCount = wishlistRepository.countLikesByProductId(w.getProduct().getId());
            res.setLikeCount((int) likeCount);
            return res;
        }).toList();
    }

    /**
     * Lấy danh sách sản phẩm được yêu thích nhiều nhất (theo lượt thích giảm dần)
     */
    public List<WishlistResponse> getTopLikedProducts() {
        List<Object[]> results = wishlistRepository.findProductsByLikeCountDesc();

        return results.stream()
                .map(obj -> {
                    Integer productId = (Integer) obj[0];
                    Long likeCount = (Long) obj[1];
                    Product product = productRepository.findById(productId).orElse(null);
                    if (product == null) return null;

                    Wishlist fake = Wishlist.builder().product(product).build();
                    WishlistResponse res = wishlistMapper.toWishlistResponse(fake);
                    res.setLikeCount(likeCount.intValue());
                    return res;
                })
                .filter(r -> r != null)
                .toList();
    }
}
