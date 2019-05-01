package dict_gen;

import java.io.File;

class DocumentParsingException extends Exception{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String FileName;

    public DocumentParsingException(File file, String message) {
        super(message);
        this.FileName = file.getName();
    }

    public DocumentParsingException(String file_name, String message) {
        super(message);
        this.FileName = file_name;
    }
    public String getMessage() {
        return String.format("Invalid document file %s. Reason: %s", this.FileName, super.getMessage());
    }
}