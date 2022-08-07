package com.decagon.decapay.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


public class PageUtil {
    public static Pageable normalisePageRequest(Pageable pageable) { //
        return  PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize());
    }

    public static Pageable normalisePageRequest(int pageNo, int pageSize) { //
        return  PageRequest.of(pageNo <= 0 ? 0 : pageNo - 1, pageSize);
    }
}
