package hu.gaszabo.kafka.message.sender.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebMvc
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	protected void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
		addDefaultHttpMessageConverters(converters);
		tryToWireObjectMapperIntoJackson2MessageConverter(converters);
	}

	private void tryToWireObjectMapperIntoJackson2MessageConverter(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter converter = tryToFindConverter(converters);
		converter.setObjectMapper(objectMapper);
	}

	private MappingJackson2HttpMessageConverter tryToFindConverter(List<HttpMessageConverter<?>> converters) {
		return MappingJackson2HttpMessageConverter.class.cast( //
				converters //
						.stream() //
						.filter(converter -> MappingJackson2HttpMessageConverter.class.isInstance(converter)) //
						.findAny() //
						.orElse(new MappingJackson2HttpMessageConverter()));
	}

}
