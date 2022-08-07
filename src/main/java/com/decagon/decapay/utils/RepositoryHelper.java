package com.decagon.decapay.utils;

import javax.persistence.Query;


public class RepositoryHelper {
    public static void addQueriesParameter(Query query, Query countQuery, String key, Object value){
        query.setParameter(key, value);
        if (countQuery!=null) {
            countQuery.setParameter(key, value);
        }
    }
}
