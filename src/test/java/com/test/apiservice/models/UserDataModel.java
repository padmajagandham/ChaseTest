package com.test.apiservice.models;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserDataModel {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private Address address;
    private String phone;
    private String website;
    private Company company;

    @Data
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public class Address {
        String street;
        private String suite;
        private String city;
        private String zipcode;
        private Geo geo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public class Geo {
        private String lat;
        private String lon;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public class Company {
        private String name;
        private String catchPhrase;
        private String bs;
    }

}

