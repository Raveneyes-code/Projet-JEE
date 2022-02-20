package org.sid.inventoryservice;

import org.sid.inventoryservice.entities.Product;
import org.sid.inventoryservice.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner start(ProductRepository productRepository, RepositoryRestConfiguration restConfiguration){
        restConfiguration.exposeIdsFor(Product.class);
        return args -> {
            productRepository.save(new Product(null,"p1",100,1));
            productRepository.save(new Product(null,"p2",31,12));
            productRepository.save(new Product(null,"p3",435,14));
            productRepository.save(new Product(null,"p4",23,16));
            productRepository.save(new Product(null,"p5",43,7));
            productRepository.findAll().forEach(c->{
                System.out.println(c.toString());
            });
        };
    }
}
