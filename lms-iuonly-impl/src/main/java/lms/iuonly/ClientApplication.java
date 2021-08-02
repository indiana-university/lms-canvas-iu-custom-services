package lms.iuonly;

import edu.iu.uits.lms.email.EnableEmailClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableEmailClient
@EnableResourceServer
@PropertySource(value = {"classpath:env.properties",
      "${app.fullFilePath}/database.properties",
      "${app.fullFilePath}/denodo.properties",
      "${app.fullFilePath}/derdack.properties",
      "${app.fullFilePath}/oauth.properties",
      "${app.fullFilePath}/services.properties",
      "${app.fullFilePath}/security.properties"}, ignoreResourceNotFound = true)
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

}
