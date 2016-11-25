package ru.velkomfood.mysap.dms.watcher.model;

/**
 * Created by dpetrov on 16.11.16.
 */
public class DMSDocument {

    private String documentType;
    private String documentNumber;
    private String documentPart;
    private String documentVersion;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentPart() {
        return documentPart;
    }

    public void setDocumentPart(String documentPart) {
        this.documentPart = documentPart;
    }

    public String getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(String documentVersion) {
        this.documentVersion = documentVersion;
    }

}
