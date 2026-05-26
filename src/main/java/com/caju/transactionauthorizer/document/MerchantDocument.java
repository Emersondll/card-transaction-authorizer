package com.caju.transactionauthorizer.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "merchant")
public class MerchantDocument {
    @Id
    private String id;
    private String name;
    private String mcc;

    public MerchantDocument(final String id, final String name, final String mcc) {
        this.id = id;
        this.name = name;
        this.mcc = mcc;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(final String mcc) {
        this.mcc = mcc;
    }
}
