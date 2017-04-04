package com.romanmayer.analyzer.dao;

import com.romanmayer.analyzer.model.Country;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CountryDaoImpl implements CountryDao {

    // Hold a reusable reference to a SessionFactory (since we need only one)
    private final SessionFactory sessionFactory = buildSessionFactory();
    private List<Country> countries;

    public CountryDaoImpl() {
        countries = fetchAllCountries();
    }

    private SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public List<Country> getCountries() {
        return countries;
    }

    private List<Country> fetchAllCountries() {

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

    @Override
    public Country findByCode(String code) {
        return countries.stream().filter(country -> country.getCode().equals(code)).findFirst().orElse(null);
    }

    private List<Country> notNullCountries() {
        return countries.stream()
                .filter(country -> country.getInternetUsers() != null && country.getAdultLiteracyRate() != null)
                .collect(Collectors.toList());
    }

    @Override
    public Country countryWithMinInternetUsers() {
        return notNullCountries().stream()
                .min(Comparator.comparingDouble(country -> country.getInternetUsers().doubleValue()))
                .get();
    }

    @Override
    public Country countryWithMaxInternetUsers() {
        return notNullCountries().stream()
                .max(Comparator.comparingDouble(country -> country.getInternetUsers().doubleValue()))
                .get();
    }

    @Override
    public Country countryWithMinLiteracyRate() {
        return notNullCountries().stream()
                .min(Comparator.comparingDouble(country -> country.getAdultLiteracyRate().doubleValue()))
                .get();
    }

    @Override
    public Country countryWithMaxLiteracyRate() {
        return notNullCountries().stream()
                .max(Comparator.comparingDouble(country -> country.getAdultLiteracyRate().doubleValue()))
                .get();
    }

    @Override
    public Double getCorrelationCoefficient() {
        double[] internetUsers = notNullCountries().stream()
                .map(Country::getInternetUsers)
                .mapToDouble(BigDecimal::doubleValue)
                .toArray();

        double[] adultLiteracyRate = notNullCountries().stream()
                .map(Country::getAdultLiteracyRate)
                .mapToDouble(BigDecimal::doubleValue)
                .toArray();

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        return pearsonsCorrelation.correlation(internetUsers, adultLiteracyRate);
    }

    @Override
    public void update(Country updatedCountry) {
        // update countries object in the DAO
        int countryIndex = countries.indexOf(findByCode(updatedCountry.getCode()));
        countries.set(countryIndex, updatedCountry);

        // update database
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(updatedCountry);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void create(Country newCountry) {
        // update countries object in the DAO
        countries.add(newCountry);

        // update database
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(newCountry);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(Country country) {
        // remove country from countries object in the DAO
        countries.remove(country);

        // delete from database
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(country);
        session.getTransaction().commit();
        session.close();
    }

}
