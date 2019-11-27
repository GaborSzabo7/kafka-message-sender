package hu.gaszabo.kafka.message.sender.data.service;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;
import static org.springframework.kafka.support.KafkaHeaders.MESSAGE_KEY;
import static org.springframework.kafka.support.KafkaHeaders.TIMESTAMP;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import hu.gaszabo.kafka.message.sender.data.Data;

@Component
public class DataMessageSender {

	private static final Logger log = LoggerFactory.getLogger(DataMessageSender.class);

	private final Environment environment;

	private final KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	public DataMessageSender(final Environment environment, final KafkaTemplate<String, String> kafkaTemplate) {
		this.environment = requireNonNull(environment, "environment must not be null");
		this.kafkaTemplate = requireNonNull(kafkaTemplate, "kafkaTemplate must not be null");
	}

	public void send(final String payload) {
		requireNonNull(payload, "payload must not be null");
		kafkaTemplate //
				.send(createMessage(new Data(payload))) //
				.addCallback(result -> {
					log.debug("Message successfully sent: {}", result);
				}, e -> {
					throw new IllegalStateException(e);
				});
	}

	public void send(final Data data) {
		kafkaTemplate //
				.send(createMessage(data)) //
				.addCallback(result -> {
					log.debug("Message successfully sent: {}", result);
				}, e -> {
					throw new IllegalStateException(e);
				});
	}

	public void sendMultipleTimes(final int multiplier, final String payload) {
		requireNonNull(multiplier, "multiplier must not be null");
		requireNonNull(payload, "payload must not be null");

		List<Data> datas = contructDataMultipleTimesWithPayload(multiplier, payload);

		datas.stream().forEach(data -> {
			kafkaTemplate //
					.send(createMessage(data)) //
					.addCallback(result -> {
						log.debug("Message successfully sent");
					}, e -> {
						throw new IllegalStateException(e);
					});
		});
	}

	private List<Data> contructDataMultipleTimesWithPayload(final int multiplier, final String payload) {
		List<Data> result = new ArrayList<>(multiplier);
		for (int i = 0; i < multiplier; i++) {
			result.add(new Data(payload));
		}
		return unmodifiableList(result);
	}

	private Message<?> createMessage(final Data data) {
		return MessageBuilder //
				.withPayload(data) //
				.setHeader(TOPIC, getTopic()) //
				.setHeader(MESSAGE_KEY, data.getId()) //
				.setHeader(CORRELATION_ID, data.getId()) //
				.setHeader(TIMESTAMP, Instant.now().toEpochMilli()) //
				.build();
	}

	private String getTopic() {
		return environment.getRequiredProperty("messaging.producer.topic", String.class);
	}

}
