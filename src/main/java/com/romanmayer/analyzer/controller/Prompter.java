package com.romanmayer.analyzer.controller;

import com.romanmayer.analyzer.dao.CountryDao;
import com.romanmayer.analyzer.model.Country;
import com.romanmayer.analyzer.model.Country.CountryBuilder;
import com.romanmayer.utility.TableBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class Prompter {

    private CountryDao countryDao;
    private BufferedReader bufferedReader;
    private Map<Integer, String> menu;

    public Prompter(CountryDao countryDao) {
        this.countryDao = countryDao;
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        menu = new HashMap<>();
        menu.put(1, "View all countries");
        menu.put(2, "View statistics for all indicators");
        menu.put(3, "View one country");
        menu.put(4, "Edit a country");
        menu.put(5, "Add a country");
        menu.put(6, "Delete a country");
        menu.put(7, "Quit");
    }

    private int promptForMenuChoice() throws IOException {
        System.out.printf("%n%nPlease select a number from %s to %s, based on what you want to do.%n", 1, menu.size());
        for (Map.Entry<Integer, String> menuItem : menu.entrySet()) {
            System.out.printf("%s: %s%n", menuItem.getKey(), menuItem.getValue());
        }
        int choice = -1;
        try {
            choice = Integer.parseInt(bufferedReader.readLine());
        } catch (NumberFormatException nfe) {
            System.out.printf("Wrong number format. Please enter a valid number from %s to %s%n", 1, menu.size());
        }
        return choice;
    }

    public void mainPrompter() {
        int choice = -1;
        do {
            try {
                choice = promptForMenuChoice();
                switch (choice) {
                    case 1:
                        viewAllCountries();
                        break;
                    case 2:
                        viewStatistics();
                        break;
                    case 3:
                        viewCountryData();
                        break;
                    case 4:
                        editCountryData();
                        break;
                    case 5:
                        addCountry();
                        break;
                    case 6:
                        deleteCountry();
                        break;
                    case 7:
                        System.out.println("\nGoodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Please try again. Pick a number between 1 and 6.\n");
                }
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } while (choice != 7);
    }

    private void deleteCountry() {
        countryDao.delete(promptForExistingCountryCode());
    }

    private void addCountry() {
        Country country = new CountryBuilder(promptForNewCountryCode(), promptForName())
                .withInternetUsers(promptForInternetUsers())
                .withLiteracyRate(promptForAdultLiteracyRate())
                .build();
        countryDao.create(country);
    }

    private String promptForNewCountryCode() {
        String code = null;
        do {
            System.out.print("Please put in the 3-letter country code: ");
            try {
                code = bufferedReader.readLine().toUpperCase();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } while (code == null || code.length() != 3);
        return code;
    }

    private void editCountryData() {
        Country updatedCountry = promptForExistingCountryCode();

        updatedCountry.setName(promptForName());
        updatedCountry.setInternetUsers(promptForInternetUsers());
        updatedCountry.setAdultLiteracyRate(promptForAdultLiteracyRate());

        countryDao.update(updatedCountry);
        System.out.println("Updating finished.");
    }

    private String promptForName() {
        String newName = null;
        do {
            System.out.printf("Please enter a new name: ");
            try {
                newName = bufferedReader.readLine();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } while (newName == null || newName.equals(""));
        return newName;
    }

    private BigDecimal promptForInternetUsers() {
        BigDecimal newInternetUsers = null;
        do {
            System.out.printf("Please enter a new value for internetUsers: ");
            try {
                //TODO:RM what if I pass an invalid value into the BigDecimal constructor?
                newInternetUsers = new BigDecimal(Double.parseDouble(bufferedReader.readLine()));
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            } catch (NumberFormatException nfe) {
                System.out.println(nfe.getMessage());
            }
        } while (newInternetUsers == null);
        return newInternetUsers;
    }

    private BigDecimal promptForAdultLiteracyRate() {
        BigDecimal adultLiteracyRate = null;
        do {
            System.out.printf("Please enter a new value for adultLiteracyRate: ");
            try {
                //TODO:RM what if I pass an invalid value into the BigDecimal constructor?
                adultLiteracyRate = new BigDecimal(Double.parseDouble(bufferedReader.readLine()));
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            } catch (NumberFormatException nfe) {
                System.out.println(nfe.getMessage());
            }
        } while (adultLiteracyRate == null);
        return adultLiteracyRate;
    }

    private void viewCountryData() {
        Country country = promptForExistingCountryCode();
        System.out.printf("%n%nYou have selected %s:%n%n", country.getName());
        TableBuilder tableBuilder = new TableBuilder();
        tableBuilder.addRow("Country", "Code  ", "Internet Users   ", "Literacy");
        tableBuilder.addRow("-----------------------------------", "-----", "-----------------", "--------");
        addCurrentCountryToTableBuilder(tableBuilder, country);
        System.out.println(tableBuilder.toString());
    }

    private Country promptForExistingCountryCode() {
        String code;
        Country country = null;

        do {
            try {
                System.out.print("Please put in the 3-letter country code: ");
                code = bufferedReader.readLine().replaceAll("\\s","").toUpperCase();
                country = countryDao.findByCode(code);
                if (country == null) {
                    viewAllCountries();
                    System.out.print("Country not found. Please enter a valid 3-letter code from the above list.");
                }
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } while (country == null);

        return country;
    }

    private void viewStatistics() {
        TableBuilder tableBuilder = new TableBuilder();
        tableBuilder.addRow("Country", "Code ", "Internet Users   ", "Literacy", "Why");
        tableBuilder.addRow("-----------------------------------", "-----", "-----------------", "--------", "-----");

        Country countryWithMinInternetUsers = countryDao.countryWithMinInternetUsers();
        addCurrentCountryToTableBuilderWithReason(tableBuilder, countryWithMinInternetUsers, "min(Internet Users)");

        Country countryWithMaxInternetUsers = countryDao.countryWithMaxInternetUsers();
        addCurrentCountryToTableBuilderWithReason(tableBuilder, countryWithMaxInternetUsers, "max(Internet Users)");

        Country countryWithMinLiteracyRate = countryDao.countryWithMinLiteracyRate();
        addCurrentCountryToTableBuilderWithReason(tableBuilder, countryWithMinLiteracyRate, "min(Literacy Rate)");

        Country countryWithMaxLiteracyRate = countryDao.countryWithMaxLiteracyRate();
        addCurrentCountryToTableBuilderWithReason(tableBuilder, countryWithMaxLiteracyRate, "max(Literacy Rate)");

        System.out.println(tableBuilder.toString());

        System.out.printf("This is the correlation coefficient between #internetUsers and #adultLiteracyRate: %s%n%n",
                countryDao.getCorrelationCoefficient());
    }

    private void viewAllCountries() {
        System.out.printf("%n%nThese are all countries in the database:%n%n");
        TableBuilder tableBuilder = new TableBuilder();
        tableBuilder.addRow("Country", "Code  ", "Internet Users   ", "Literacy");
        tableBuilder.addRow("-----------------------------------", "-----", "-----------------", "--------");
        for (Country country : countryDao.getCountries()) {
            addCurrentCountryToTableBuilder(tableBuilder, country);
        }
        System.out.println(tableBuilder.toString());
    }

    private void addCurrentCountryToTableBuilder(TableBuilder tableBuilder, Country country) {
        String name = country.getName();
        String code = country.getCode();
        String internetUsers;
        if (country.getInternetUsers() == null) {
            internetUsers = "--";
        } else {
            internetUsers = country.getInternetUsers().setScale(2, RoundingMode.HALF_UP).toString();
        }

        String adultLiteracyRate;
        if (country.getAdultLiteracyRate() == null) {
            adultLiteracyRate = "--";
        } else {
            adultLiteracyRate = country.getAdultLiteracyRate().setScale(2, RoundingMode.HALF_UP).toString();
        }
        tableBuilder.addRow(name, code, internetUsers, adultLiteracyRate);
    }

    private void addCurrentCountryToTableBuilderWithReason(TableBuilder tableBuilder, Country country, String reason) {
        String name = country.getName();
        String code = country.getCode();
        String internetUsers = country.getInternetUsers().setScale(2, RoundingMode.HALF_UP).toString();
        String adultLiteracyRate = country.getAdultLiteracyRate().setScale(2, RoundingMode.HALF_UP).toString();

        tableBuilder.addRow(name, code, internetUsers, adultLiteracyRate, reason);
    }

}
