package com.decagon.decapay.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Spring pageable's pageNumber by default start at offset 0. Normally page request parameter should
 * start from 1 ("page=1") rather than zero ("page=0") for readability. To make page request parameter to start
 * at 1 in the client request, spring pageable's data needs to be normalised by recalculating (decrementing)
 * requested pageNumber to sync with spring's pageable class format
 *
 */

public class PageUtil {
    public static Pageable normalisePageRequest(Pageable pageable) { //
        return  PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize());
    }

    public static Pageable normalisePageRequest(int pageNo, int pageSize) { //
        return  PageRequest.of(pageNo <= 0 ? 0 : pageNo - 1, pageSize);
    }
}
