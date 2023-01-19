package com.test.apiservice.models;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonInclude;
import io.cucumber.java.DataTableType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PostDataModel {
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Integer id;
   private Integer userId;
   private String title;
   private String body;
}
