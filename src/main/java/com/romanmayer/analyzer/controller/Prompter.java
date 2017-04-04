package com.romanmayer.analyzer.controller;

import com.romanmayer.analyzer.dao.CountryDao;
import com.romanmayer.analyzer.model.Country;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by romanmayer on 04/04/2017.
 */
public class Prompter {

    private CountryDao countryDao;
    private List<Country> countries;
    private BufferedReader reader;
    private Map<Integer, String> menu;

    public Prompter(CountryDao countryDao) {
        this.countryDao = countryDao;
        countries = new ArrayList<>();
        countries = countryDao.fetchAllCountries();
    }

}
