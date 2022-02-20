package org.sid.custormerservice;

import org.sid.custormerservice.entities.Customer;
import org.sid.custormerservice.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@SpringBootApplication
public class CustormerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustormerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(CustomerRepository customerRepository, RepositoryRestConfiguration restConfiguration){
        restConfiguration.exposeIdsFor(Customer.class);
        return args -> {
            customerRepository.save(new Customer(null,"Aiman","mail1.gmail"));
            customerRepository.save(new Customer(null,"Anass","mail2.gmail"));
            customerRepository.save(new Customer(null,"Yahya","mail3.gmail"));
            customerRepository.save(new Customer(null,"Benomar","mail4.gmail"));
            customerRepository.findAll().forEach(c->{
                System.out.println(c.toString());
            });
        };
    }
}
