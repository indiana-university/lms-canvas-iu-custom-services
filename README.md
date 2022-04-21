# LMS Canvas IU Custom Services

When this library is added to a project it allows for various IU customized services.

## Installation
### From Maven
Add the following as a dependency in your pom.xml
```xml
<dependency>
    <groupId>edu.iu.uits.lms</groupId>
    <artifactId>lms-canvas-iu-custom-services</artifactId>
    <version><!-- latest version --></version>
</dependency>
```

You can find the latest version in [Maven Central](https://search.maven.org/search?q=g:edu.iu.uits.lms%20AND%20a:lms-canvas-iu-custom-services).

## Setup Examples
### Include annotation to enable to the configs
Add to any configuration class, or even the main application class `@EnableIuOnlyClient`.

Once that has been done, you can autowire in and use the various services:

```java
@Autowired
private FeatureAccessServiceImpl featureAccessService;

@Autowired
private SudsServiceImpl sudsService;
```

## Configuration
If choosing to use properties files for the configuration values, the default location is `/usr/src/app/config`, but 
that can be overridden by setting the `app.fullFilePath` value via system property or environment variable.

### Database Configuration
The following properties need to be set to configure the communication with a database.
They can be set in a security.properties file, or overridden as environment variables.

| Property              | Default Value | Description                                                                   |
|-----------------------|---------------|-------------------------------------------------------------------------------|
| `lms.db.user`         |               | Username used to access the database                                          |
| `lms.db.url`          |               | JDBC URL of the database.  Will have the form `jdbc:<host>:<port>/<database>` | 
| `lms.db.driverClass`  |               | JDBC Driver class name                                                        |
| `lms.db.password`     |               | Password for the user accessing the database                                  |

### Denodo Configuration (optional)
The following properties need to be set to configure the communication with Denodo.
They can be set in a security.properties file, or overridden as environment variables.
Additionally, you will need to enable it by including the value `denodo` into the `SPRING_PROFILES_ACTIVE` environment 
variable. Be aware that if the tool requires multiple values, that there could be more than one profile value in there.

| Property                 | Default Value | Description                                       |
|--------------------------|---------------|---------------------------------------------------|
| `denodo.db.driverClass`  |               | Driver class for denodo database connections      |
| `denodo.db.url`          |               | JDBC URL for connecting to denodo                 |
| `denodo.db.user`         |               | Denodo username                                   |
| `denodo.db.password`     |               | Denodo password                                   |

### Derdack Configuration (optional)
The following properties need to be set to configure the communication with Derdack.
They can be set in a security.properties file, or overridden as environment variables.
Additionally, you will need to enable it by including the value `derdack` into the `SPRING_PROFILES_ACTIVE` environment
variable. Be aware that if the tool requires multiple values, that there could be more than one profile value in there.

| Property                 | Default Value                             | Description                          |
|--------------------------|-------------------------------------------|--------------------------------------|
| `derdack.rest.baseUrl`   |                                           | Base URL for Derdack API             |
| `derdack.rest.apiKey`    |                                           | Derdack API key                      |
| `derdack.rest.team`      |                                           | Team for Derdack requests            |
| `derdack.recipientEmail` | iu-uits-es-ess-lms-notify@exchange.iu.edu | Email for Derdack notifications      |

### Exposing the REST endpoints
If you would like to expose the REST endpoints in a tool, you will need to enable them by including the value 
`iucustomrest` into the `SPRING_PROFILES_ACTIVE` environment variable. Be aware that if the tool requires multiple values, 
that there could be more than one profile value in there.

#### OAuth2 requirements
In order to get access to the endpoints, you'd need to configure an OAuth2 server.  Once setup, the user(s) needing access
would have to be granted the `iusvcs:read` and/or the `iusvcs:write` scopes as appropriate.  Grant type should be set as `Authorization Code`.

#### REST endpoint documentation
See the [wiki](wiki/API-Endpoint-Documentation) for details.

#### Dev notes
To generate the REST docs (asciidoc) that live in the github wiki, take the following steps:
1. Enable the rest endpoints and swagger in the tool of choice and start it up
2. Note the api docs url.  Should be something like http://localhost:8080/api/iu/v3/api-docs
3. Download the openapi-generator from [here](https://openapi-generator.tech/docs/installation)
4. Run the following (:warning: *Command could be slightly different based on your OS and install method*):
   `openapi-generator generate -g asciidoc -i <url_from_above_step>`
5. Take the generated output and update the wiki page (:warning: *Some hand editing may be required*)
