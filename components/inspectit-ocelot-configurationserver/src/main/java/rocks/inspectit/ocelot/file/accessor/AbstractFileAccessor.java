package rocks.inspectit.ocelot.file.accessor;

import lombok.extern.slf4j.Slf4j;
import rocks.inspectit.ocelot.file.FileInfo;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Abstract class providing a read access to the configuration files and agent mappings.
 */
@Slf4j
public abstract class AbstractFileAccessor {

    /**
     * The default file encoding of all files.
     */
    public static final Charset FILE_ENCODING = StandardCharsets.UTF_8;

    /**
     * The file name of the agent mappings.
     */
    public static final String AGENT_MAPPINGS_FILE_NAME = "agent_mappings.yaml";

    /**
     * The subfolder within the working directory which acts as
     * filesRoot for the files and directories managed by this class.
     */
    public static final String CONFIGURATION_FILES_SUBFOLDER = "files";

    /**
     * Verifies the given path and checks if it is located beneath the given base path. It has to be ensured, that
     * the given path is not navigate out of the given base path, resulting in a traversal attack. A cleaned and
     * sanitized path is returned of this method, which can safely be used be the concrete implementation for file
     * handling.
     *
     * @param relativeBasePath the base path where the path must be in
     * @param path             the relative user path
     * @return a sanitized representation of the given user path
     * @throws IllegalArgumentException is thrown if the path is not valid.
     */
    protected abstract String verifyPath(String relativeBasePath, String path) throws IllegalArgumentException;

    /**
     * Reads and returns the file's content which is located under the given path.
     *
     * @param path the file to read
     * @return the content of the file
     */
    protected abstract Optional<byte[]> readFile(String path);

    /**
     * Returns a list of all files and directories located under the specified path.
     *
     * @param path the root path to start listing
     * @return a list of files and directories
     */
    protected abstract List<FileInfo> listFiles(String path);

    /**
     * @return Checks whether the given path exists.
     */
    protected abstract boolean exists(String path);

    /**
     * @return Checks whether the given path is a directory.
     */
    protected abstract boolean isDirectory(String path);

    /**
     * Reads the file content of the specified configuration file. In case the file does not exist or cannot be read,
     * the resulting {@link Optional} will be empty.
     *
     * @param file the configuration file to read
     * @return the content of the specified file
     */
    public Optional<String> readConfigurationFile(String file) {
        log.debug("Reading configuration file: {}", file);
        String targetPath = verifyPath(CONFIGURATION_FILES_SUBFOLDER, file);

        Optional<byte[]> fileContent = readFile(targetPath);

        return fileContent.map(String::new);
    }

    /**
     * Lists all configuration files and directories which are located under the specified path
     * (also in sub directories). The listing will be resolved recursively.
     *
     * @param path a list of the files in the specified directory
     * @return a list of existing files and directories
     */
    public List<FileInfo> listConfigurationFiles(String path) {
        log.debug("Listing configuration files: {}", path);
        String targetPath = verifyPath(CONFIGURATION_FILES_SUBFOLDER, path);

        return listFiles(targetPath);
    }

    /**
     * @return Returns the content of the agent mappings file.
     */
    public Optional<String> readAgentMappings() {
        log.debug("Reading agent mappings");

        Optional<byte[]> fileContent = readFile(AGENT_MAPPINGS_FILE_NAME);

        return fileContent.map(bytes -> new String(bytes, FILE_ENCODING));
    }

    /**
     * @return Returns whether the given path exists.
     */
    public boolean configurationFileExists(String path) {
        String targetPath = verifyPath(CONFIGURATION_FILES_SUBFOLDER, path);

        return exists(targetPath);
    }

    /**
     * @return Returns <code>true</code> if the given path is a directory, otherwise <code>false</code>.
     */
    public boolean configurationFileIsDirectory(String path) {
        String targetPath = verifyPath(CONFIGURATION_FILES_SUBFOLDER, path);

        return isDirectory(targetPath);
    }
}
