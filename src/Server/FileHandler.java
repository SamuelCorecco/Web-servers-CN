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
    //This is the server path.
    private static final String serverPath = Path.of("").toAbsolutePath().toString();
    //This is a mapping from each possible (in this server) file extension to its corresponding MIME type
    private static Map<String, String> acceptedFileTypes = Map.ofEntries(
            entry("txt", "text/plain"),
            entry("css", "text/css"),
            entry("html", "text/html"),
            entry("pdf", "application/pdf"),
            entry("jpeg", "image/jpeg"),
            entry("jpg", "image/jpeg"),
            entry("png", "image/png")
    );

    public static String getMimeType(String filename) {
        String ext = getFileExtension(filename);
        return extensionToMimeType(ext);
    }

    /**
     * It returns the MIME type of the extension.
     * @param extension is a String representing the extension of the file
     * @return the MIME type mapped to the extension passed as argument, in case the extension is not recognized the default value "text/plain" is returned
     */

    public static String extensionToMimeType(String extension) {
        if (acceptedFileTypes.get(extension.toLowerCase()).isEmpty()) {
            return "text/plain";
        } else {
            return acceptedFileTypes.get(extension.toLowerCase());
        }
    }

    /**
     * It deletes the desired file.
     * @param host is the host
     * @param URL is the URL
     * @throws FileNotFoundException if the file corresponding to the URL for the host passed in the argument cannot be found
     */

    public static void deleteFile(String host, String URL) throws FileNotFoundException {
        File fileToDelete = new File(host, URL);
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     * It creates a file for the given host, corresponding to the passed URL and with the given body.
     * @param host the host for which the file is going to be created
     * @param URL the URL of the file to be created
     * @param body the body of the file to be created
     * @return true if the file has been created, false if it was already existing (edited)
     * @throws IOException if there was an error in creating or writing to the file
     */

    public static boolean createFile(String host, String URL, byte[] body) throws IOException {
        File file = new File(host, URL);
        boolean hasBeenCreated = file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(body);
        fos.close();
        return hasBeenCreated;
    }

    /**
     * It retrieves the content of the file located at host + URL, relative to this server path.
     * @param host is the host
     * @param URL is the URL
     * @return the content of the file
     * @throws IOException if there was an error in managing the file
     */

    public static byte[] getFileContent(String host, String URL) throws IOException {
        String requestedFileName = serverPath + "/" + host + "/" + URL;
        return Files.readAllBytes(Paths.get(requestedFileName));
    }

    /**
     * It says if the file is existing or not.
     * @param host is the host
     * @param URL is the URL
     * @return true if the file exists, false otherwise
     */

    public static boolean checkFileExists(String host, String URL) {
        File file = new File(serverPath, host + "/" + URL);
        return file.exists();
    }

    /**
     * It returns the extension of the file from its corresponding URL.
     * @param URL the URL corresponding to the file
     * @return a String corresponding to the file extension, in case the URL is "/" it returns the default value "text/html"
     */

    public static String getFileExtension(String URL) {
        String[] splitURL = URL.split("\\.");
        return splitURL[splitURL.length - 1];
    }

    /**
     * It says if the host has the permission to perform the action.
     * Note that in case host + URL do not form a valid URL the method returns false,
     * so use this method only after having checked the validity of host + URL.
     * @param host the host
     * @param URL the URL
     * @return true if the host has the permission to perform the action, false otherwise
     */

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
