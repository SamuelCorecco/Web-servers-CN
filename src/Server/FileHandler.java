package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.util.Map.entry;

public class FileHandler {
    private static final String serverPath = Path.of("").toAbsolutePath().toString();
    private static Map<String, String> acceptedFileTypes = Map.ofEntries(
            entry("txt", "text/plain"),
            entry("css", "text/css"),
            entry("html", "text/html"),
            entry("pdf", "application/pdf"),
            entry("jpeg", "image/jpeg"),
            entry("jpg", "image/jpeg"),
            entry("png", "image/png")
    );

    public static String extensionToMimeType(String extension) {
        if (acceptedFileTypes.get(extension.toLowerCase()).isEmpty()) {
            return "text/plain";
        } else {
            return acceptedFileTypes.get(extension.toLowerCase());
        }
    }

    public static void deleteFile(String host, String URL) throws FileNotFoundException {
        File fileToDelete = new File(host, URL);
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        } else {
            throw new FileNotFoundException();
        }
    }

    public static boolean createFile(String host, String URL, byte[] body) throws IOException {
        File file = new File(host, URL);
        boolean success = file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(body);
        fos.close();
        return success;
    }

    public static byte[] getFileContent(String host, String URL) throws IOException {
        String requestedFileName = serverPath + "/" + host + URL;
        return Files.readAllBytes(Paths.get(requestedFileName));
    }

    public static boolean existingFile(String host, String URL) {
        File file = new File(serverPath, host + URL);
        return file.exists();
    }

    public static String getMimeType(String URL) {
        String[] splitURL = URL.split("\\.");
        if (URL.equals("/")) {
            return "text/html";
        } else {
            return splitURL[splitURL.length - 1];
        }
    }

    public static boolean validPermission(String host, String URL) {
        try {
            String canonicalObjectPath = new File(serverPath, "/" + host + "/" + URL).getCanonicalPath();
            String canonicalHostPath = new File(serverPath, "/" + host).getCanonicalPath();
            return canonicalObjectPath.startsWith(canonicalHostPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
