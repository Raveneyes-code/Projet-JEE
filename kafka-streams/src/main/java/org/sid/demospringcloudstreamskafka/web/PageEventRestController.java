package org.sid.demospringcloudstreamskafka.web;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.sid.demospringcloudstreamskafka.entities.PageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/* Permet de publier des msg à topic kafka suite à un évènement */

@RestController
public class PageEventRestController {
    // Utilisé solution Spring cloud streams qui permet de publier msg à topic
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private InteractiveQueryService interactiveQueryService;  //permet l'interaction avec store

    // Permet de publier un pageEvent
    @GetMapping("/publish/{topic}/{namePage}")
    public PageEvent publish(@PathVariable String topic, @PathVariable String namePage){
        //Produit msg
        PageEvent pageEvent = new PageEvent(namePage, Math.random()>0.5?"User1":"User2", new Date(), new Random().nextInt(9000));
        //Envoyer msg
        streamBridge.send(topic, pageEvent);
        return pageEvent;
    }

    //Afficher les statistiques (nbr de visites pour chaque page(P1 et P2) dans partie web
    @GetMapping(path = "/analytics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Long>> analytics(){
        return Flux.interval(Duration.ofSeconds(1)) //répéter chaque 1s
                .map(sequence->{
                    Map<String, Long> stringLongMap = new HashMap<>();
                    //Récupérer store
                    ReadOnlyWindowStore<String, Long> windowStore = interactiveQueryService.getQueryableStore("page-count", QueryableStoreTypes.windowStore());
                    Instant now = Instant.now();  //get time now
                    Instant from = now.minusMillis(5000);  // = now-5s
                    KeyValueIterator<Windowed<String>, Long> fetchAll = windowStore.fetchAll(from, now);  //Get statistic de 5s dernières
                    while (fetchAll.hasNext()){    //Parcouris fetchAll pour get les clés and value
                        KeyValue<Windowed<String>, Long> next = fetchAll.next();
                        stringLongMap.put(next.key.key(), next.value);
                    }
                    return stringLongMap;
                }).share();   //afficher le m résultat (flux) pour tous les users (!chaque user va recoive en fonction de son moment de connexion)
    }
}
