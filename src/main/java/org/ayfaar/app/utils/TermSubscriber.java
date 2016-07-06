package org.ayfaar.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.services.itemRange.ItemRangeService;
import org.springframework.stereotype.Component;
import rx.Subscriber;
import javax.inject.Inject;

@Slf4j
@Component
public class TermSubscriber extends Subscriber<String>{

    @Inject ItemRangeService itemRangeService;
    @Inject TermsFinder termsFinder;
    @Inject TermService termService;

    @Override
    public void onCompleted() {
        log.info("All Terms updates");
        log.info("All TermParagraphs updates");
    }

    @Override
    public void onError(Throwable throwable) {
        new RuntimeException("Update of Terms and Services is not available");
    }

    @Override
    public void onNext(String s) {
        termService.reload();
        termsFinder.updateTermParagraphForTerm(s);
        itemRangeService.reload();
    }
}
