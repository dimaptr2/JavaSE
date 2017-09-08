package ru.velkomfood.dms.info.model;

public class Document {

    private long id;
    private String externalNumber;
    private String docType;
    private String docPart;
    private String version;
    private String description;
    private String user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExternalNumber() {
        return externalNumber;
    }

    public void setExternalNumber(String externalNumber) {
        this.externalNumber = externalNumber;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocPart() {
        return docPart;
    }

    public void setDocPart(String docPart) {
        this.docPart = docPart;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document document = (Document) o;

        if (id != document.id) return false;
        if (!externalNumber.equals(document.externalNumber)) return false;
        return docType.equals(document.docType);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + externalNumber.hashCode();
        result = 31 * result + docType.hashCode();
        return result;
    }

}
