/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: micha
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.application.kafka;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import de.bomc.poc.hrm.application.log.method.Loggable;
import lombok.extern.slf4j.Slf4j;

/**
 * A kafka consumer.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 28.12.2019
 */
@Slf4j
@Component
public class HrmKafkaConsumer {

	private static final String LOG_PREFIX = HrmKafkaConsumer.class + "#";

	@Loggable(time = true)
	@KafkaListener(id = "${kafka.topic.consumer.group-id}", clientIdPrefix = "bomc", topics = "${kafka.topic.data.name}", containerFactory = "concurrentKafkaListenerContainerFactory", properties = { "enable.auto.commit=true", "auto.commit.interval.ms=1000", "poll-interval=100"})
	public void listen(final ConsumerRecord<String, String> consumerRecord) {
		
		consumerRecord.headers().forEach(header -> {
			final String value = new String(header.value(), StandardCharsets.UTF_8);
			
			log.info(LOG_PREFIX + "listen [header.key=" + header.key() + ", header.value=" + value + "]");
		});
		
		log.info(LOG_PREFIX + "listen [consumerRecord=" + consumerRecord + ", consumerRecord.value=" + consumerRecord.value() + "]");
	}
}
