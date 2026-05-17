package com.caju.transactionauthorizer.document;

import com.caju.transactionauthorizer.enums.CategoryCodeName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mcc")
public class MerchantCategoryCodesDocument {
    @Id
    private String id;
    private String code;
    private CategoryCodeName description;

    public MerchantCategoryCodesDocument(final String id, final String code, final CategoryCodeName description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public CategoryCodeName getDescription() {
        return description;
    }

    public void setDescription(final CategoryCodeName description) {
        this.description = description;
    }
}
