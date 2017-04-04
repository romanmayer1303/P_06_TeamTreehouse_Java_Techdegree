package com.romanmayer.analyzer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Country {

    @Id
    @Column(length = 3)
    private String code;

    @Column(length = 32)
    private String name;

    @Column(columnDefinition = "Decimal(11,8)")
    private BigDecimal internetUsers;

    @Column(columnDefinition = "Decimal(11,8)") // "... default '100.00'"
    private BigDecimal adultLiteracyRate;

    // Default constructor for JPA
    public Country() {
    }

    public Country(CountryBuilder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.internetUsers = builder.internetUsers;
        this.adultLiteracyRate = builder.adultLiteracyRate;
    }

    /*
        @Override
        public String toString() {
            return "Country{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", internetUsers=" + internetUsers +
                    ", adultLiteracyRate=" + adultLiteracyRate +
                    '}';
        }
    */

    public String toString() {
        return name + "\t\t\t\t\t\t\t\t\t" +
                internetUsers + "\t\t\t\t\t\t" +
                adultLiteracyRate;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(BigDecimal internetUsers) {
        this.internetUsers = internetUsers;
    }

    public BigDecimal getAdultLiteracyRate() {
        return adultLiteracyRate;
    }

    public void setAdultLiteracyRate(BigDecimal adultLiteracyRate) {
        this.adultLiteracyRate = adultLiteracyRate;
    }

    public static class CountryBuilder {

        private String code;
        private String name;
        private BigDecimal internetUsers;
        private BigDecimal adultLiteracyRate;

        public CountryBuilder(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public CountryBuilder withInternetUsers(BigDecimal internetUsers) {
            this.internetUsers = internetUsers;
            return this;
        }

        public CountryBuilder withLiteracyRate(BigDecimal adultLiteracyRate) {
            this.adultLiteracyRate = adultLiteracyRate;
            return this;
        }

        public Country build() {
            return new Country(this);
        }

    }
}
