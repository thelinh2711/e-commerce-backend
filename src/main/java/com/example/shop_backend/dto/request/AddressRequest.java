package com.example.shop_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    @NotBlank(message = "Vui lòng nhập số nhà và tên đường")
    private String street;

    @NotBlank(message = "Vui lòng chọn phường/xã")
    private String ward;

    @NotBlank(message = "Vui lòng chọn quận/huyện")
    private String district;

    @NotBlank(message = "Vui lòng chọn tỉnh/thành phố")
    private String province;

    private String note;

    @JsonProperty("isDefault")
    private boolean defaultAddress;
}
