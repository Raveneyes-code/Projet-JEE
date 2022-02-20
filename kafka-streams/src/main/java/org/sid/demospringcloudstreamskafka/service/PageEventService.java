package org.sid.demospringcloudstreamskafka.service;

/*permet de consommer et publier les msg en utilisant programmation fonctionnel*/

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.processor.internals.RecordCollector;
import org.sid.demospringcloudstreamskafka.entities.PageEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class PageEventService {
    //permet de consommer les objets de type PageEvent à partir de kafka
    @Bean //pour que cette function soit déployé
    public Consumer<PageEvent> pageEventConsumer(){
        return (input)->{
            System.out.println("*********************");
            System.out.println(input.toString());
            System.out.println("*********************");
        };
    }
    //chaque 1s (par défaut configuré) va produit un evenement (R2)
    @Bean
    public Supplier<PageEvent> pageEventSupplier(){
        return ()-> new PageEvent(
                Math.random()>0.5?"P1":"P2",
                Math.random()>0.5?"User1":"User2",
                new Date(),
                new Random().nextInt(9000));
    }
    //lire à partir d'un topic R2 et transférer vers un autre R3
    @Bean
    public Function<PageEvent, PageEvent> pageEventFunction() {
        return (input) -> {
            input.setName("Page Event");
            input.setUser("UUUU");
            return input;
        };
    }

    //Faire des traitements en temps réel (en utilisant kafka streams)
    //en entré, on a <nomPage, Page Event>, et la sortie doit etre <nomPage, sommeDuration dans cet page>
  /*  @Bean
    public Function <KStream<String,PageEvent>, KStream<String,Long>> kStreamKStreamFunction(){
        return (input)->{
          return input
                  .filter((k,v)->v.getDuration()>100)  //on prend en entré seulement les valeurs > 100 ms
                  .map((k,v)->new KeyValue<>(v.getName(),0L))  //spécifier sortie
                  .groupBy((k,v)->k, Grouped.with(Serdes.String(),Serdes.Long()))  //grouper par key, et spécifier le type de key et le type de value
                  .windowedBy(TimeWindows.of(5000)) //afficher les statistiques de 5s dernières
                  .count(Materialized.as("page-count"))   //produit store qui stocke ces données afin de l'afficher dans partie web
                  .toStream()    //la sortie va etre géneré en toStream
                  .map((k,v)->new KeyValue<>("=>"+k.window().startTime()+k.window().endTime()+k.key(),v));   //afficher en chaine de caractère
        };
    }  */

    //Faire des traitements en temps réel (en utilisant kafka streams)
    //en entré, on a <nomPage, Page Event>, et la sortie doit etre <nomPage, sommeDuration (somme visite) dans cet page>
    @Bean
    public Function<KStream<String,PageEvent>, KStream<String,Long>> kStreamFunction(){
        return (input)->{
            return input
                    .filter((k,v)->v.getDuration()>100) //on prend en entré seulement les valeurs > 100 ms
                    .map((k,v)->new KeyValue<>(v.getName(),0L))  //spécifier sortie
                    .groupBy((k,v)->k,Grouped.with(Serdes.String(),Serdes.Long()))  ////grouper par key, et spécifier le type de key et le type de value
                    .windowedBy(TimeWindows.of(5000))   //afficher les statistiques de 5s dernières
                    .count(Materialized.as("page-count"))   //produit store qui stocke ces données afin de l'afficher dans partie web
                    .toStream()  //la sortie va etre géneré en toStream
                    .map((k,v)->new KeyValue<>("=>"+k.window().startTime()+k.window().endTime()+":"+k.key(),v));   //afficher en chaine de caractère
        };
    }

}
