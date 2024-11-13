package com.example.appvehiculos.ui.documentos;

public class Document {
    private String userId;
    private String fileUrl;
    private boolean isImage;

    // Constructor sin argumentos requerido para Firebase
    public Document() {
        // Default constructor required for calls to DataSnapshot.getValue(Document.class)
    }

    // Constructor para crear un nuevo documento
    public Document(String userId, String fileUrl, boolean isImage) {
        this.userId = userId;
        this.fileUrl = fileUrl;
        this.isImage = isImage;
    }

    public String getUserId() {
        return userId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public boolean isImage() {
        return isImage;
    }
}
