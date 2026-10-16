package com.campusrecruit.web.dto;
public record OtpChallengeResponse(String message,long expiresInSeconds,String delivery,String developmentCode) {}
