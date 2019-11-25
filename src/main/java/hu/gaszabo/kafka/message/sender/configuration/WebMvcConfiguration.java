package hu.gaszabo.kafka.message.sender.configuration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebMvc
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment environment;

	@Override
	protected void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
		addDefaultHttpMessageConverters(converters);
		tryToWireObjectMapperIntoJackson2MessageConverter(converters);
	}

	@Override
	public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
		super.configureAsyncSupport(configurer);
		configurer.setDefaultTimeout(MILLISECONDS.convert(30, SECONDS));
		configurer.setTaskExecutor(asyncTaskExecutor());
	}

	@Bean
	public AsyncTaskExecutor asyncTaskExecutor() {
		final String threadNamePrefix = //
				this.environment.getProperty("taskexecutor.threadnameprefix", String.class, "asyncTaskExecutor");
		final int maxpoolsize = this.environment.getProperty("taskexecutor.maxpoolsize", Integer.class, 4);
		final int corepoolsize = this.environment.getProperty("taskexecutor.corepoolsize", Integer.class, 2);
		final int queuecapacity = this.environment.getProperty("taskexecutor.queuecapacity", Integer.class, 4);
		return createTaskExecutor(threadNamePrefix, maxpoolsize, corepoolsize, queuecapacity);
	}

	private void tryToWireObjectMapperIntoJackson2MessageConverter(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter converter = tryToFindConverter(converters);
		converter.setObjectMapper(this.objectMapper);
	}

	private MappingJackson2HttpMessageConverter tryToFindConverter(List<HttpMessageConverter<?>> converters) {
		return MappingJackson2HttpMessageConverter.class.cast( //
				converters //
						.stream() //
						.filter(converter -> MappingJackson2HttpMessageConverter.class.isInstance(converter)) //
						.findAny() //
						.orElse(new MappingJackson2HttpMessageConverter()));
	}

	private AsyncTaskExecutor createTaskExecutor( //
			final String threadNamePrefix, final int maxpoolsize, final int corepoolsize, final int queuecapacity) {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setMaxPoolSize(maxpoolsize);
		executor.setCorePoolSize(corepoolsize);
		executor.setQueueCapacity(queuecapacity);
		executor.setAwaitTerminationSeconds(1);
		executor.setThreadNamePrefix(threadNamePrefix);
		return executor;
	}

}
