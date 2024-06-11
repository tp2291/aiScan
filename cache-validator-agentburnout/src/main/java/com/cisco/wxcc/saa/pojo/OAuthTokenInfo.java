package com.cisco.wxcc.saa.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OAuthTokenInfo {
    private long expiresIn;
    private String tokenType;
    private String accessToken;
    private String trackingId;
    private long refreshTokenExpiresIn;
    private String refreshToken;
    private String scope;
    private int accountExpiration;
    private long accessTokenValidUntil;
    private long refreshTokenValidUntil;


}