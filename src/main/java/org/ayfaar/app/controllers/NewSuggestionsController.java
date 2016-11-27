package org.ayfaar.app.controllers;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.services.ItemService;
import org.ayfaar.app.services.document.DocumentService;
import org.ayfaar.app.services.images.ImageService;
import org.ayfaar.app.services.record.RecordService;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.services.videoResource.VideoResourceService;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.SearchSuggestions;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.UriGenerator;
import org.ayfaar.app.utils.contents.ContentsUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;

@Slf4j
@RestController
@RequestMapping("api/suggestions")
public class NewSuggestionsController {

    @Inject
    SearchSuggestions searchSuggestions;

    public Map<String, String> suggestions(String q) {
        return suggestions(q, false, false, false, false, false, false, false, false, false, false);
    }

    @RequestMapping("all")
    @ResponseBody
    public Map<String, String> suggestions(@RequestParam String q,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_terms,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_topic,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_category_name,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_category_description,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_doc,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_video,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_item,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_record_name,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_record_code,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_images
    ) {

        return searchSuggestions.suggestions(q, with_terms,with_topic, with_category_name, with_category_description, with_doc,
                                                    with_video, with_item, with_record_name, with_record_code, with_images, false);
    }

}
