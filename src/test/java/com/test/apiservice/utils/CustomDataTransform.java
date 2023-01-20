package com.test.apiservice.utils;

import com.test.apiservice.models.PostDataModel;
import io.cucumber.java.DataTableType;

import java.util.Map;

public class CustomDataTransform {

    /** converts table rows in Gherkin specification to PostDataModel object
     *so that it is easier to use in step definitions
     */
    @DataTableType()
    public PostDataModel postReqTransformer(Map<String, String> row) {
        return new PostDataModel(
                null,
                Integer.parseInt(row.get("userId")),
                row.get("title"),
                row.get("body")
        );
    }
}
