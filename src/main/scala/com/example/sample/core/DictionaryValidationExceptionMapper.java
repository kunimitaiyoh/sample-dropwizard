package com.example.sample.core;

import com.google.common.collect.Iterables;
import io.dropwizard.jersey.validation.ConstraintMessage;
import io.dropwizard.jersey.validation.JerseyViolationException;
import io.dropwizard.validation.ValidationMethod;
import org.glassfish.jersey.server.validation.internal.LocalizationMessages;
import org.glassfish.jersey.server.validation.internal.ValidationHelper;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.*;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

@SuppressWarnings("WeakerAccess")
public final class DictionaryValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    static final Logger LOGGER = Logger.getLogger(DictionaryValidationExceptionMapper.class.getName());

    final Config config;

    public DictionaryValidationExceptionMapper(Config config) {
        this.config = config;
    }

    @Override
    public Response toResponse(ValidationException exception) {
        if (exception instanceof JerseyViolationException) {
            LOGGER.log(Level.FINER, LocalizationMessages.CONSTRAINT_VIOLATIONS_ENCOUNTERED(), exception);

            final JerseyViolationException ex = (JerseyViolationException) exception;
            Object entity = toEntity(ex, this.config);
            return Response.status(ValidationHelper.getResponseStatus(ex))
                    .type(MediaType.APPLICATION_JSON)
                    .entity(entity)
                    .build();
        } else {
            LOGGER.log(Level.WARNING, LocalizationMessages.VALIDATION_EXCEPTION_RAISED(), exception);
            return Response.serverError().entity(exception.getMessage()).build();
        }
    }

    public static DictionaryValidationExceptionMapper withDefault() {
        return new DictionaryValidationExceptionMapper(Config.DEFAULT);
    }

    static Object toEntity(JerseyViolationException exception, Config conf) {
        Collection<ConstraintViolation<?>> violations = exception.getConstraintViolations();


        Map<String, List<Object>> fieldErrors = new HashMap<>();
        List<Object> generalErrors = new ArrayList<>();

        for (ConstraintViolation v : violations) {
            Path path = v.getPropertyPath();
            Object element = conf.violationMapper.apply(v, exception);
            boolean isMethod = v.getConstraintDescriptor().getAnnotation() instanceof ValidationMethod;
            if (isMethod || Iterables.size(path) < 2) {
                generalErrors.add(element);
            } else {
                String name = String.join(".", () -> StreamSupport.stream(Iterables.skip(path, 2).spliterator(), false).<CharSequence>map(Path.Node::getName).iterator());
                fieldErrors.computeIfAbsent(name, k -> new ArrayList<>()).add(element);
            }
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put(conf.fieldAttributeName, fieldErrors);
        payload.put(conf.generalAttributeName, generalErrors);

        Map<String, Object> response = new HashMap<>();
        response.put(conf.attributeName, payload);
        return response;
    }

    public static class Config {
        public static final Config DEFAULT = new Config("errors", "fields", "general", Config::defaultMapper);

        final String attributeName;
        final String fieldAttributeName;
        final String generalAttributeName;
        final BiFunction<ConstraintViolation, JerseyViolationException, Object> violationMapper;

        public Config(
                @NotNull String attributeName,
                @NotNull String fieldAttributeName,
                @NotNull String generalAttributeName,
                @NotNull BiFunction<ConstraintViolation, JerseyViolationException, Object> violationMapper) {
            this.attributeName = attributeName;
            this.fieldAttributeName = fieldAttributeName;
            this.generalAttributeName = generalAttributeName;
            this.violationMapper = violationMapper;
        }

        static Object defaultMapper(ConstraintViolation v, JerseyViolationException e) {
            Map<String, Object> element = new HashMap<>();
            String message = ConstraintMessage.getMessage(v, e.getInvocable());
            element.put("message", message);
            return element;
        }
    }
}
