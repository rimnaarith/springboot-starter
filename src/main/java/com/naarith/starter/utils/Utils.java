package com.naarith.starter.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public abstract class Utils {
    public  static URI makeUrl(String path) {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path(path).toUriString());
    }
    public static String makeUploadUrl(String path) {
        return makeUrl("uploads/" + path).toString();
    }
}
