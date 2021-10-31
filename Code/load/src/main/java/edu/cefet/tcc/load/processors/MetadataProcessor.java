package edu.cefet.tcc.load.processors;

import java.util.Properties;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;

import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import edu.cefet.tcc.domain.shared.Data;

@Component
public class MetadataProcessor {
    private static final String APPLICATION_ID = "app-metadata-store-processor";
    
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${edu.cefet.tcc.load.metadata-processor.input-topic}")
    private String FILTERED_DATA_INPUT;

    @Value("${spring.kafka.bootstrap.servers}")
    private String HOST_ADDRESS;

    @Bean("MetadataProcessorStreamBuilder")
    public StreamsBuilderFactoryBean ProcessadorNovosTweetsStreamBuilderFactoryBean() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_ADDRESS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass());
    
        StreamsBuilderFactoryBean sb = new StreamsBuilderFactoryBean(); 
        sb.setStreamsConfiguration(props);
        return sb;
    }
    
    @Bean("MetadataProcessorStreamTopology")
    public KTable<String, Long> startProcessing(
        @Qualifier("MetadataProcessorStreamBuilder") StreamsBuilder builder
    ) { 
        return builder.stream(
            FILTERED_DATA_INPUT, 
            Consumed.with(
                Serdes.String(),
                Serdes.Long()
            )
        ).toTable(Materialized.as("metadataStore"));
    }
}
