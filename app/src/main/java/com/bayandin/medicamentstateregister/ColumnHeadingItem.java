package com.bayandin.medicamentstateregister;

public class ColumnHeadingItem {

    private final String sqlId;
    private final String textHeading;

    public ColumnHeadingItem(String textHeading, String sqlId) {
        this.textHeading = textHeading;
        this.sqlId = sqlId;
    }

    public ColumnHeadingItem() {
        this.textHeading = "Международное наименование";
        this.sqlId = "internationalName";
    }

    public String getSqlId() {
        return sqlId;
    }

    public String getTextHeading() {
        return textHeading;
    }

}
