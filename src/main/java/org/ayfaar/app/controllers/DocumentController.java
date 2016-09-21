package org.ayfaar.app.controllers;

import com.google.api.services.drive.model.File;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Document;
import org.ayfaar.app.model.Image;
import org.ayfaar.app.utils.GoogleService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.ayfaar.app.utils.UriGenerator.generate;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.util.Assert.hasLength;

@RestController
@RequestMapping("api/document")
//todo: rename, update author etc
public class DocumentController {
    @Inject CommonDao commonDao;
    @Inject GoogleService googleService;

    @RequestMapping(method = RequestMethod.POST)
    public Document create(@RequestParam String url,
                           @RequestParam(required = false) Optional<String> name,
                           @RequestParam(required = false) String author,
                           @RequestParam(required = false) String annotation) {
        Assert.hasLength(url);
        final String docId = GoogleService.extractDocIdFromUrl(url);
        return commonDao.getOpt(Document.class, generate(Document.class, docId))
            .orElseGet(() -> {
                final GoogleService.DocInfo docInfo = googleService.getDocInfo(docId);
                String docName = name.orElse(docInfo.title);
                String currentDocId = docId;
                String currentUrl = docInfo.downloadUrl;
                String mimeType = docInfo.mimeType;
                String thumbnailLink = docInfo.thumbnailLink;
                String iconLink = docInfo.iconLink;

                if(!url.contains("google.com")){
                    try {
                        File file = googleService.uploadToGoogleDrive(url, docName);
                        currentDocId = file.getId();
                        currentUrl = file.getAlternateLink();
                        mimeType = file.getMimeType();
                        thumbnailLink = file.getThumbnailLink();
                        iconLink = file.getIconLink();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                final Document document = Document.builder()
                        .id(currentDocId)
                        .name(docName)
                        .annotation(annotation)
                        .author(author)
                        .thumbnail(thumbnailLink)
                        .mimeType(mimeType)
                        .icon(iconLink)
                        .downloadUrl(currentUrl)
                        .build();
                return commonDao.save(document);
            });

    }

    @RequestMapping("{id}")
    public Document get(@PathVariable String id) {
        return commonDao.get(Document.class, generate(Document.class, id));
    }

    @RequestMapping("last")
    public List<Document> getLast(@PageableDefault(size = 9, sort = "createdAt", direction = DESC) Pageable pageable) {
        return commonDao.getPage(Document.class, pageable);
    }

    @RequestMapping(value = "update-name", method = RequestMethod.POST)
    public void updateTitle(@RequestParam String uri, @RequestParam String title) {
        hasLength(uri);
       Document document = commonDao.get(Document.class, "uri", uri);
        if (document != null) {
            document.setName(title);
            commonDao.save(document);
        }
    }
}
