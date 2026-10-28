package com.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.jpa.HibernatePersistenceProvider;

import com.entity.Employee;
import com.impl.PersistenceUnitInfoImpl;

public class Launch {

	public static void main(String[] args) {
		
		Properties properties = new Properties();
		InputStream is = null;
		
		try {
			is = Launch.class.getClassLoader().getResourceAsStream("config.properties");
			
			if (is == null) {
				System.out.println("File not found");
				return;
			}
			
			properties.load(is);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if (is != null) {
				
				try {
					is.close();
					
				} catch (IOException e) {
					e.printStackTrace();
					
				}
			}
		}
		
		Map<String, String> map = new HashMap<>();
		
		for (String key : properties.stringPropertyNames()) {
			map.put(key, properties.getProperty(key));
			
		}
		
		// So here we are not using any xml file, but we are taking control using config.properties, where we store the property
		// So here, we need to create an obj of HibernatePersistenceProvider and call the createContainerEntityManagerFactory method
		// that takes the PersistenceUnitInfoImpl obj and a map that we have created using the properties
		
		EntityManagerFactory emf = new HibernatePersistenceProvider()
				.createContainerEntityManagerFactory(new PersistenceUnitInfoImpl(), map);
		
		EntityManager em = emf.createEntityManager();
		
		insert(em);
		read(em);
		update(em);
		delete(em);
		
		em.close();
		emf.close();
		
	}

	private static void delete(EntityManager em) {
		
		Employee employee = em.find(Employee.class, 12);
		
		if (employee != null) {
			EntityTransaction transaction = em.getTransaction();
			transaction.begin();
			
			em.remove(employee); // remove the employee
			transaction.commit();
			
			System.out.println("Employee deleted...");
			
		} else {
			System.out.println("Employee id not found...");
		}
		
	}

	private static void update(EntityManager em) {
		
		Employee employee = em.find(Employee.class, 12);
		
		if (employee != null) {
			EntityTransaction transaction = em.getTransaction();
			transaction.begin();
			
			employee.setEsalary(5000);
			
			em.merge(employee); // Here we set the salary of this obj to 5000, and then merge the changes of the obj to table
			transaction.commit();
			
			System.out.println(employee);
			System.out.println("Data changed...");
			
		} else {
			System.out.println("Employee id not found...");
		}
		
	}

	private static void read(EntityManager em) {
		
		Employee employee = em.find(Employee.class, 12);
		
		if (employee != null) {
			System.out.println("Read: " + employee);
			
		} else {
			System.out.println("Data not found...");
			
		}
		
	}

	private static void insert(EntityManager em) {
		// Unlike JDBC that auto commits whenever a query is run, in Hibernate, we need to always begin a transaction and commit it
		// to make changes reflected in the DB
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		
		Employee employee = new Employee(12, "ABCDEF", "gsfkshdfkah", 3000);
		em.persist(employee);
		
		transaction.commit();
		System.out.println("Data Inserted...");
	}

}
