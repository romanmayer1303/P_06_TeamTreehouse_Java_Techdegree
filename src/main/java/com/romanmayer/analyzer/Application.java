package com.romanmayer.analyzer;

import com.romanmayer.analyzer.model.Country;
import com.romanmayer.utility.TableBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class Application {

    // Hold a reusable reference to a SessionFactory (since we need only one)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
//        listAllCountries();
        List<Country> countries = getStatistics();
        countries.forEach(System.out::println);
    }

    private static void listAllCountries() {
        List<Country> countries = fetchAllCountries();

        System.out.println();
        System.out.println("These are all countries in the database:");
        System.out.println();

        TableBuilder tableBuilder = new TableBuilder();
        tableBuilder.addRow("Country", "Internet Users   ", "Literacy");
        tableBuilder.addRow("-----------------------------------", "-----------------", "--------");
        for (Country country : countries) {
            String name = country.getName();

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

            tableBuilder.addRow(name, internetUsers, adultLiteracyRate);
        }
        System.out.println(tableBuilder.toString());
//        fetchAllCountries().forEach(System.out::println);
    }

    private static List<Country> fetchAllCountries() {

        // Open a session
        Session session = sessionFactory.openSession();

        // UPDATED: Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // UPDATED: Create CriteriaQuery
        CriteriaQuery<Country> criteria = builder.createQuery(Country.class);

        // UPDATED: Specify criteria root
        criteria.from(Country.class);

        // UPDATED: Execute query
        List<Country> countries = session.createQuery(criteria).getResultList();

        // Close the session
        session.close();

        return countries;
    }

    private static List<Country> getStatistics() {
        // Open a session
        Session session = sessionFactory.openSession();

        // Retrieve the persistent object (or null if not found)
        String queryString = "SELECT c FROM Country c WHERE internetUsers IS NOT NULL AND "
                + "adultLiteracyRate IS NOT NULL order by internetUsers asc"; // WHERE literacy IS NULL";

        Query query = session.createQuery(queryString);
        List<Country> countries = query.getResultList();
        Country countryWithMinInternetUsers = countries.get(0);
        Country countryWithMaxInternetUsers = (Country)query.getResultList().get(countries.size()-1);
        List<Country> result = new ArrayList<>();
        result.add(countryWithMinInternetUsers);
        result.add(countryWithMaxInternetUsers);

        queryString = "SELECT c FROM Country c WHERE internetUsers IS NOT NULL AND "
                + "adultLiteracyRate IS NOT NULL order by adultLiteracyRate asc";

        query = session.createQuery(queryString);
        countries = query.getResultList();
        Country countryWithMinLiteracyRate = countries.get(0);
        Country countryWithMaxLiteracyRate = countries.get(countries.size()-1);


        result.add(countryWithMinLiteracyRate);
        result.add(countryWithMaxLiteracyRate);

        TableBuilder tableBuilder = new TableBuilder();
        tableBuilder.addRow("Country", "Internet Users   ", "Literacy", "Why");
        tableBuilder.addRow("-----------------------------------", "-----------------", "--------", "-----");
        tableBuilder.addRow(countryWithMinInternetUsers.getName(),
                countryWithMinInternetUsers.getInternetUsers().toString(),
                countryWithMinInternetUsers.getAdultLiteracyRate().toString(),
                "min(Internet Users)");
        tableBuilder.addRow(countryWithMaxInternetUsers.getName(),
                countryWithMaxInternetUsers.getInternetUsers().toString(),
                countryWithMaxInternetUsers.getAdultLiteracyRate().toString(),
                "max(Internet Users)");
        tableBuilder.addRow(countryWithMinLiteracyRate.getName(),
                countryWithMinLiteracyRate.getInternetUsers().toString(),
                countryWithMinLiteracyRate.getAdultLiteracyRate().toString(),
                "min(Literacy Rate)");
        tableBuilder.addRow(countryWithMaxLiteracyRate.getName(),
                countryWithMaxLiteracyRate.getInternetUsers().toString(),
                countryWithMaxLiteracyRate.getAdultLiteracyRate().toString(),
                "max(Literacy Rate)");
        System.out.println(tableBuilder.toString());


        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        queryString = "SELECT c FROM Country c WHERE "
                + "internetUsers IS NOT NULL AND "
                + "adultLiteracyRate IS NOT NULL";
        query = session.createQuery(queryString);
        countries = query.getResultList();

        double[] internetUsers = countries.stream()
                .map(Country::getInternetUsers)
                .mapToDouble(BigDecimal::doubleValue)
                .toArray();

        double[] adultLiteracyRate = countries.stream()
                .map(Country::getAdultLiteracyRate)
                .mapToDouble(BigDecimal::doubleValue)
                .toArray();

        double correlationCoefficient = pearsonsCorrelation.correlation(internetUsers, adultLiteracyRate);

        System.out.printf("This is the correlation coefficient between #internetUsers and #adultLiteracyRate: %s%n%n",
                correlationCoefficient);

        // Close session
        session.close();

        // Return the object
        return result;
    }


}
