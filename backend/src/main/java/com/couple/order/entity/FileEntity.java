package com.couple.order.entity;

import lombok.Data;

@Data
public class FileEntity {
    private Long id;
    private String filename;
    private String originalName;
    private String contentType;
    private Long size;
    private String data; // Base64 encoded
    private String url;
    private String createdAt;
}
