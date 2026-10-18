package model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import jakarta.servlet.http.Part;

public class UploadFile {

    private String name;
    private String extention;
    private InputStream inputStream;
    private String fileDestinationName;

    public UploadFile(Part part) throws IOException {
        this.name = part.getSubmittedFileName();
        this.inputStream = part.getInputStream();
        // 1. Extraire l'extension
        int i = name.lastIndexOf('.');
        if (i > 0) {
            this.extention = name.substring(i + 1);
        }
        setFileDestinationName();
    }

    private void setFileDestinationName() {
        // TODO : A RECUPERER DANS UN PROPRETIES
        this.fileDestinationName = "/home/max/Fianarana/main/s4/webdin/projet/upload/";
    }

    public void saveFile() throws IOException {
        File fileUpload = new File(this.fileDestinationName.concat(name));
        System.out.println("Nom du fichier  : " + fileDestinationName);
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
