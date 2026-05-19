package com.couple.order.controller;

import com.couple.order.entity.ApiResponse;
import com.couple.order.entity.FileEntity;
import com.couple.order.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<FileEntity> upload(@RequestParam("file") MultipartFile file) {
        try {
            FileEntity fileEntity = fileService.uploadFile(file);
            return ApiResponse.ok(fileEntity);
        } catch (IOException e) {
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<FileEntity>> getAllFiles() {
        return ApiResponse.ok(fileService.getAllFiles());
    }

    @GetMapping("/{id}")
    public ApiResponse<FileEntity> getById(@PathVariable Long id) {
        FileEntity file = fileService.getFileById(id);
        if (file == null) return ApiResponse.error("文件不存在");
        file.setData(null); // Don't return base64 data in list
        return ApiResponse.ok(file);
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<byte[]> getContent(@PathVariable Long id) {
        FileEntity file = fileService.getFileById(id);
        if (file == null) return ResponseEntity.notFound().build();

        byte[] content = fileService.getFileContent(id);
        if (content == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType() != null ? file.getContentType() : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getOriginalName() + "\"")
                .body(content);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ApiResponse.ok();
    }
}
