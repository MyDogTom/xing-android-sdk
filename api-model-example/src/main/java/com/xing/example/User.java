package com.xing.example;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.xing.api.annotations.CompanionForJson;
import com.xing.example.Company.CompanyFieldsCompanion;

@AutoValue
@CompanionForJson
public abstract class User {

    @Json(name ="first_name")
    public abstract String firstName();

    @Json(name = "last_name")
    public abstract String lastName();

    @Json(name = "company")
    public abstract Company company();

    public static Builder builder() {
        return new AutoValue_User.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        abstract Builder firstName(String firstName);

        abstract Builder lastName(String lastName);

        abstract Builder company(Company company);

        abstract User build();
    }


    public static UserCompanion companion() {
        return new UserCompanion();
    }

    public static class UserCompanion extends Companion{

        public UserCompanion requestFirstName() {
            addField("first_name");
            return this;
        }

        public UserCompanion requestLastName() {
            addField("last_name");
            return this;
        }


        public UserCompanion requestCompany() {
            addField("company");
            return this;
        }

        public UserCompanion requestCompany(CompanyFieldsCompanion companion) {
            addField(companion.buildSubFields("company"));
            return this;
        }
    }




}
