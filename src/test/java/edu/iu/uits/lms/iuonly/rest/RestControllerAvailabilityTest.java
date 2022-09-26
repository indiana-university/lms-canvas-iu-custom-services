package edu.iu.uits.lms.iuonly.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.iu.uits.lms.iuonly.IuCustomConstants.IUCUSTOMREST_PROFILE;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.INHERIT;

@NestedTestConfiguration(INHERIT)
@SpringBootTest
public class RestControllerAvailabilityTest {

   private static final String PACKAGE = "edu.iu.uits.lms.iuonly";

   @Nested
   @ActiveProfiles({IUCUSTOMREST_PROFILE})
   class Enabled {

      @Autowired
      private ApplicationContext appContext;

      @Test
      public void ensureBeansAreLoaded() {
         Set<Class<?>> typesAnnotatedWith = new Reflections(PACKAGE).getTypesAnnotatedWith(RestController.class);
         List<String> restControllerClassNames = typesAnnotatedWith.stream().map(Class::getName).collect(Collectors.toList());

         Map<String, Object> beansWithAnnotation = appContext.getBeansWithAnnotation(RestController.class);
         List<String> beanClassNames = beansWithAnnotation.values().stream()
               .map(Object::getClass)
               .map(ClassUtils::getUserClass)
               .map(Class::getName)
               .collect(Collectors.toList());

         boolean allMatch = restControllerClassNames.stream().allMatch(beanClassNames::contains);

         Assertions.assertTrue(allMatch);
      }
   }

   @Nested
   class Disabled {
      @Autowired
      private ApplicationContext appContext;

      @Test
      public void ensureBeansNotLoaded() {
         Set<Class<?>> typesAnnotatedWith = new Reflections(PACKAGE).getTypesAnnotatedWith(RestController.class);
         List<String> restControllerClassNames = typesAnnotatedWith.stream().map(Class::getName).collect(Collectors.toList());

         Map<String, Object> beansWithAnnotation = appContext.getBeansWithAnnotation(RestController.class);
         List<String> beanClassNames = beansWithAnnotation.values().stream()
               .map(Object::getClass)
               .map(Class::getName)
               .collect(Collectors.toList());

         boolean noneMatch = beanClassNames.stream().noneMatch(foo -> restControllerClassNames.contains(foo));

         Assertions.assertTrue(noneMatch, "Found a rest controller that is available even though it shouldn't be");
      }
   }

}
