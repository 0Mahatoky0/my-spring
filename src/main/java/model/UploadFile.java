package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.naming.NameNotFoundException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;

public class UploadFile {

    private String name;
    private String extention;
    private InputStream inputStream;

    private String uploadDir;

    public UploadFile(Part part,ServletContext context) throws IOException, NameNotFoundException {
        this.name = part.getSubmittedFileName();
        this.inputStream = part.getInputStream();
        // 1. Extraire l'extension
        int i = name.lastIndexOf('.');
        if (i > 0) {
            this.extention = name.substring(i + 1);
        }
        
        if(context.getAttribute("fileUploadDir") != null) {
            uploadDir = context.getAttribute("fileUploadDir").toString();
        } else {
            throw new NameNotFoundException("[ERROR] : la configuration de 'fileUploadDir' n est pas trouver. Initialiser le dans app.properties");
        }
    }

    public void saveFile() throws IOException {
        File fileUpload = new File(this.uploadDir.concat(name));
        System.out.println("Nom du fichier  : " + uploadDir);
        Files.copy(this.inputStream, fileUpload.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public String getName() {
        return name;
    }

    public String getExtention() {
        return extention;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
