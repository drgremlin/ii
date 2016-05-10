package org.ayfaar.app.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.getProperty;

@Component
public class GoogleService {
    @Value("${google.api.key}") private String API_KEY;

    @Value("${drive-dir}") private String dirStore;

    @Inject RestTemplate restTemplate;

    private static final String APPLICATION_NAME = "MyApp";

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Directory to store user credentials. */
    private static java.io.File DATA_STORE_DIR;

    private static FileDataStoreFactory dataStoreFactory;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global Drive API client. */
    private static Drive drive;

    private ResourceLoader resourceLoader;

    @Inject
    public GoogleService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public VideoInfo getVideoInfo(String id) {
        final Map response = restTemplate.getForObject("https://content.googleapis.com/youtube/v3/videos?part={part}&id={id}&key={key}",
                Map.class, "snippet", id, API_KEY);
        //noinspection unchecked
        final List<Map> items = (List<Map>) response.get("items");
        if (items.isEmpty()) throw new RuntimeException("Video private or removed");
        final Map snippet = (Map) items.get(0).get("snippet");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return new VideoInfo((String) snippet.get("title"), dateFormat.parse((String) snippet.get("publishedAt")));
        } catch (ParseException e) {
            throw new RuntimeException("Video date parsing error", e);
        }
    }

    public static String extractVideoIdFromYoutubeUrl(String url) {
        //https://www.youtube.com/watch?v=044VwC_uptU
        Matcher matcher = Pattern.compile("^https?://www\\.youtube\\.com/watch\\?v=([^&]+)").matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            // https://youtu.be/1I1cy6z-FgY
            matcher = Pattern.compile("^https?://youtu.be/(.*)$").matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        throw new RuntimeException("Cannot resolve video id");
    }

    public static String extractDocIdFromUrl(String url) {
        //https://docs.google.com/document/d/1iWY8qI5Qn1V_90VpzfhFDTyAcNgati9u6sTv-A-gWQg/edit?usp=sharing
        //https://drive.google.com/file/d/0BwGttgSD-WcTbTJFbWplN1hwcFU/view?usp=sharing
        Matcher matcher = Pattern.compile("^https?://(docs|drive)\\.google\\.com/(document|file)/d/([^/]+)").matcher(url);
        if (matcher.find()) {
            return matcher.group(3);
        }
        throw new RuntimeException("Cannot resolve document id");
    }

    public DocInfo getDocInfo(String id) {
        final DocInfo doc = restTemplate.getForObject("https://www.googleapis.com/drive/v2/files/{id}?key={key}", DocInfo.class, id, API_KEY);
        Assert.notNull(doc.title);
        return doc;
    }

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(GoogleService.class.getResourceAsStream("/client_secrets.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
                            + "into drive-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public void uploadToGoogleDrive(String url){

        Resource resource = resourceLoader.getResource("file:" + dirStore);
        if (!resource.exists()) {
            throw new RuntimeException("Error locating dir "+dirStore);
        }

        try {
            DATA_STORE_DIR = resource.getFile();
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            // authorization
            Credential credential = authorize();
            // set up the global Drive instance
            drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                    APPLICATION_NAME).build();
            // run command
            File uploadedFile = uploadFile(false, url);
            return;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    /** Uploads a file using either resumable or direct media upload. */
    private static File uploadFile(boolean useDirectUpload, String url) throws IOException {

        InputStream data = null;
        try {
            data = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File fileMetadata = new File();
        fileMetadata.setTitle(extractName(url));

        InputStreamContent mediaContent =
                new InputStreamContent("audio/mpeg",
                        new BufferedInputStream(data));

        Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);

        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(useDirectUpload);
        return insert.execute();
    }

    public static String extractName(String url) {

        Matcher matcher = Pattern.compile("([^\\\\/:*?\"<>|\r\n]+$)").matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("Cannot resolve file id");
    }

    public static class VideoInfo {
        public Date publishedAt;
        public String title;

        public VideoInfo(String title, Date publishedAt) {
            this.title = title;
            this.publishedAt = publishedAt;
        }
    }

    @NoArgsConstructor
    public static class DocInfo {
        public String title;
        public String iconLink;
        public String thumbnailLink;
        public String mimeType;
        public String downloadUrl;
        public String fileExtension;
        public Integer fileSize;
    }
}
