package com.xing.example;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.xing.example.Address.AddressCompanion;

@AutoValue
public abstract class Company {
    @Json(name ="name")
    public abstract String name();

    @Json(name = "address")
    public abstract Address address();

    public static Builder builder() {
        return new AutoValue_Company.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        abstract Builder name(String name);

        abstract Builder address(Address address);

        abstract Company build();

    }

    public static CompanyFieldsCompanion companion() {
        return new CompanyFieldsCompanion();
    }

    public static class CompanyFieldsCompanion extends Companion {
        public CompanyFieldsCompanion requestName() {
            addField("name");
            return this;
        }

        public CompanyFieldsCompanion requestAddress() {
            addField("address");
            return this;
        }

        public CompanyFieldsCompanion requestAddress(AddressCompanion companion) {
            addField(companion.buildSubFields("address"));
            return this;
        }
    }


}
