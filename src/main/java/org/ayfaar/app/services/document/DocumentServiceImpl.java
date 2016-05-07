package org.ayfaar.app.services.document;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component()
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired CommonDao commonDao;

    List<Document> allDocuments;

    @PostConstruct
    private void init() {
        logger.info("Documents loading...");

        allDocuments = commonDao.getAll(Document.class);

        logger.info("Documents loaded");
    }

    @Override
    public void reload() {
        init();
    }
    @Override
    public Map<String, String> getAllUriNames(){
        return allDocuments.stream().collect(Collectors.toMap(document -> document.getUri(),document -> document.getName()));
    }
}
