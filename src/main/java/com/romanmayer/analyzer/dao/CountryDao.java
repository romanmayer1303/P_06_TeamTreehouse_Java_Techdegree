package com.romanmayer.analyzer.dao;

import com.romanmayer.analyzer.model.Country;

import java.util.List;

/**
 * Created by romanmayer on 04/04/2017.
 */
public interface CountryDao {
    List<Country> getCountries();
    Country findByCode(String code);
    Country countryWithMinInternetUsers();
    Country countryWithMaxInternetUsers();
    Country countryWithMinLiteracyRate();
    Country countryWithMaxLiteracyRate();
    Double getCorrelationCoefficient();
    void update(Country country);
    void create(Country country);
    void delete(Country country);

}
