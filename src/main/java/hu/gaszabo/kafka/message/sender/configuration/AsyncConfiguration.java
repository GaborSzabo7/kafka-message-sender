package hu.gaszabo.kafka.message.sender.configuration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

	@Autowired
	private Environment environment;

	@Bean
	protected WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
				configurer.setDefaultTimeout(MILLISECONDS.convert(30, SECONDS));
				configurer.setTaskExecutor((AsyncTaskExecutor) getAsyncExecutor());
			}
		};
	}

	@Bean
	@Override
	public Executor getAsyncExecutor() {
		final String threadNamePrefix = //
				environment.getProperty("taskexecutor.threadnameprefix", String.class, "asyncTaskExecutor");
		final int maxpoolsize = environment.getProperty("taskexecutor.maxpoolsize", Integer.class, 4);
		final int corepoolsize = environment.getProperty("taskexecutor.corepoolsize", Integer.class, 2);
		final int queuecapacity = environment.getProperty("taskexecutor.queuecapacity", Integer.class, 4);
		return createTaskExecutor(threadNamePrefix, maxpoolsize, corepoolsize, queuecapacity);
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
