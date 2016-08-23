package com.xing.example;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    public void name() throws Exception {
        User user1 = User.builder()
              .firstName("Fake")
              .lastName("Last")
              .company(Company.builder()
                    .name("companyXYZ")
                    .address(new AutoValue_Address("Hamburg"))
                    .build())
              .build();
        User user2 = User.builder()
              .firstName("Fake")
              .lastName("Last")
              .company(Company.builder()
                    .name("companyXYZ")
                    .address(new AutoValue_Address("Hamburg"))
                    .build())
              .build();
        assertThat(user1).isEqualTo(user2);

    }

    @Test
    public void shouldGenerateFields() throws Exception {
        assertThat(
              User.companion()
                    .requestFirstName()
                    .requestLastName()
                    .buildFields()).isEqualTo("first_name,last_name");

    }

    @Test
    public void shouldGenerateWholeCompany() throws Exception {
        assertThat(
              User.companion()
                    .requestFirstName()
                    .requestLastName()
                    .requestCompany()
                    // FIXME: 23/08/16 this doesn't check that each field is separated by comma
                    .buildFields()).isEqualTo("first_name,last_name,company");

    }

    @Test
    public void shouldHaveSubField() throws Exception {
        assertThat(
              User.companion()
                    .requestCompany()
                    .requestCompany(Company.companion().requestName())
                    .requestFirstName().buildFields()
        ).isEqualTo("company,company.name,first_name");

    }

    @Test
    public void shouldGenerateTwiceSubFields() throws Exception {
        assertThat(User.companion()
              .requestFirstName()
              .requestCompany(Company.companion()
                    .requestAddress()
                    .requestAddress(Address.companion().city()))
        .buildFields()).isEqualTo("first_name,company.address,company.address.city");

    }
}