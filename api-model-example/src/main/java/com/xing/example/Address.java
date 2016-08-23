package com.xing.example;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;

@AutoValue
public abstract class Address {
    @Json(name="city")
    public abstract String city();

    public static AddressCompanion companion() {
        return new AddressCompanion();
    }

    public static class AddressCompanion extends Companion {
        AddressCompanion() {
        }

        public AddressCompanion city() {
            addField("city");
            return this;
        }
    }



}
