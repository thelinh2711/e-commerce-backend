package com.example.shop_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Integer id;
    private String street;
    private String ward;
    private String district;
    private String province;
    private String note;

    @JsonProperty("isDefault")
    private boolean defaultAddress;
}
