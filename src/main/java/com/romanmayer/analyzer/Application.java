package com.romanmayer.analyzer;

import com.romanmayer.analyzer.controller.Prompter;
import com.romanmayer.analyzer.dao.CountryDaoImpl;

public class Application {

    public static void main(String[] args) {
        Prompter prompter = new Prompter(new CountryDaoImpl());
        prompter.mainPrompter();
    }

}
