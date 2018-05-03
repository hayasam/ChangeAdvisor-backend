package ch.uzh.ifi.seal.changeadvisor;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableBatchProcessing
public class ChangeadvisorApplication {

    private final JobRepository jobRepository;

    @Autowired
    public ChangeadvisorApplication(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ChangeadvisorApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(100);
        threadPoolTaskExecutor.setMaxPoolSize(100);
        return threadPoolTaskExecutor;
    }

    @Bean
    public JobLauncher jobLauncher() {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setTaskExecutor(taskExecutor());
        launcher.setJobRepository(jobRepository);
        return launcher;
    }
}
