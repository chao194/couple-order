package com.couple.order.service;

import com.couple.order.entity.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private D1DatabaseService d1;

    public FileEntity uploadFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
        String base64Data = Base64.getEncoder().encodeToString(file.getBytes());

        String sql = "INSERT INTO file (filename, original_name, content_type, size, data) VALUES (?, ?, ?, ?, ?)";
        d1.execute(sql, filename, file.getOriginalFilename(), file.getContentType(),
                file.getSize(), base64Data);

        List<Map<String, Object>> rows = d1.query("SELECT last_insert_rowid() as id");
        Long newId = ((Number) rows.get(0).get("id")).longValue();

        FileEntity fileEntity = getFileById(newId);
        fileEntity.setUrl("/api/files/" + newId + "/content");
        return fileEntity;
    }

    public FileEntity getFileById(Long id) {
        String sql = "SELECT * FROM file WHERE id = ?";
        List<Map<String, Object>> rows = d1.query(sql, id);
        if (rows.isEmpty()) return null;
        return mapToFileEntity(rows.get(0));
    }

    public byte[] getFileContent(Long id) {
        FileEntity file = getFileById(id);
        if (file == null || file.getData() == null) return null;
        return Base64.getDecoder().decode(file.getData());
    }

    public List<FileEntity> getAllFiles() {
        String sql = "SELECT id, filename, original_name, content_type, size, url, created_at FROM file ORDER BY created_at DESC";
        List<Map<String, Object>> rows = d1.query(sql);
        return rows.stream().map(row -> {
            FileEntity file = new FileEntity();
            file.setId(((Number) row.get("id")).longValue());
            file.setFilename((String) row.get("filename"));
            file.setOriginalName((String) row.get("original_name"));
            file.setContentType((String) row.get("content_type"));
            file.setSize(((Number) row.get("size")).longValue());
            file.setUrl("/api/files/" + row.get("id") + "/content");
            file.setCreatedAt((String) row.get("created_at"));
            return file;
        }).toList();
    }

    public void deleteFile(Long id) {
        d1.execute("DELETE FROM file WHERE id = ?", id);
    }

    private FileEntity mapToFileEntity(Map<String, Object> row) {
        FileEntity file = new FileEntity();
        file.setId(((Number) row.get("id")).longValue());
        file.setFilename((String) row.get("filename"));
        file.setOriginalName((String) row.get("original_name"));
        file.setContentType((String) row.get("content_type"));
        file.setSize(row.get("size") != null ? ((Number) row.get("size")).longValue() : 0);
        file.setData((String) row.get("data"));
        file.setUrl("/api/files/" + row.get("id") + "/content");
        file.setCreatedAt((String) row.get("created_at"));
        return file;
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot >= 0 ? filename.substring(lastDot) : "";
    }
}
