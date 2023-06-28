package ElectermSync;

import java.util.Map;

public class ReadResult {
    public final Map<String, Object> fileData;
    public final int statusCode;

    ReadResult(Map<String, Object> fileData, int statusCode) {
        this.fileData = fileData;
        this.statusCode = statusCode;
    }

    ReadResult(String errorMessage, int statusCode) {
        this.fileData = Map.of("error", errorMessage);
        this.statusCode = statusCode;
    }
}
