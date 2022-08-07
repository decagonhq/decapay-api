package com.decagon.decapay.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


@Component
public class TxnManager {

    @Autowired
    private PlatformTransactionManager transactionManager;
    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
    private TransactionStatus status;

    public void endTxn() {
        transactionManager.commit(status);
    }

    public void startTxn() {
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        status = transactionManager.getTransaction(definition);
    }
}