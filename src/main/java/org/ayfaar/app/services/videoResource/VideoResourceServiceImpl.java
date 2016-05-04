package org.ayfaar.app.services.videoResource;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.VideoResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component()
public class VideoResourceServiceImpl implements VideoResourceService {
    private static final Logger logger = LoggerFactory.getLogger(VideoResourceServiceImpl.class);
    @Autowired
    CommonDao commonDao;

    List<VideoResource> allVideoResource;

    @PostConstruct
    private void init() {
        logger.info("Video resources loading...");

        allVideoResource = commonDao.getAll(VideoResource.class);

        logger.info("Video resources loaded");
    }
    @Override
    public Map<String, String> getAllUriNames() {
        return allVideoResource.stream().collect(Collectors.toMap(videoResource ->
                videoResource.getUri(),videoResource -> videoResource.getTitle()));
    }
}
