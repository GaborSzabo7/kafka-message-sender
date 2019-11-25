package hu.gaszabo.kafka.message.sender.configuration;

import static org.springframework.util.ObjectUtils.nullSafeToString;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableKafka
@Configuration
public class KafkaConfiguration {

	@Autowired
	private ConsumerFactory<String, String> consumerfactory;

	@Autowired
	private ProducerFactory<String, String> producerFactory;

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(this.consumerfactory);
		factory.setMessageConverter(new StringJsonMessageConverter(this.objectMapper));
		factory.setErrorHandler(new LoggingErrorHandler());
		return factory;
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		KafkaTemplate<String, String> template = new KafkaTemplate<>(this.producerFactory);
		template.setMessageConverter(new StringJsonMessageConverter(this.objectMapper));
		return template;
	}

	static class LoggingErrorHandler implements ErrorHandler {

		private static final Logger log = LoggerFactory.getLogger(LoggingErrorHandler.class);

		@Override
		public void handle(final Exception thrownException, final ConsumerRecord<?, ?> record) {
			log.error("Error while processing: " + nullSafeToString(record), thrownException);
		}

	}

}
