package com.example.sample.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import io.dropwizard.jersey.validation.ConstraintMessage;
import io.dropwizard.jersey.validation.JerseyViolationException;
import io.dropwizard.validation.ValidationMethod;
import org.glassfish.jersey.server.validation.internal.LocalizationMessages;
import org.glassfish.jersey.server.validation.internal.ValidationHelper;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
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

        List<Object> errors = new ArrayList<>();
        for (ConstraintViolation v : violations) {
            Path path = v.getPropertyPath();

            boolean isMethod = v.getConstraintDescriptor().getAnnotation() instanceof ValidationMethod;
            if (isMethod || Iterables.size(path) < 2) {
                Object element = conf.violationMapper.map(v, null, exception);
                errors.add(element);
            } else {
                String name = String.join(".", () -> StreamSupport.stream(Iterables.skip(path, 2).spliterator(), false).<CharSequence>map(Path.Node::getName).iterator());
                Object element = conf.violationMapper.map(v, name, exception);
                errors.add(element);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("validationProtocol", "0.0");
        response.put("method", "violations");
        response.put(conf.attributeName, errors);
        return response;
    }

    public static class Config {
        public static final Config DEFAULT = new Config("errors", Config::defaultMapper);

        final String attributeName;
        final ViolationMapper violationMapper;

        public Config(
                @NotNull String attributeName,
                @NotNull ViolationMapper violationMapper) {
            this.attributeName = attributeName;
            this.violationMapper = violationMapper;
        }

        static Object defaultMapper(ConstraintViolation v, @Nullable String fieldName, JerseyViolationException e) {
            Map<String, Object> element = new HashMap<>();
            String message = ConstraintMessage.getMessage(v, e.getInvocable());
            ConstraintDescriptor<?> descriptor = v.getConstraintDescriptor();

            Map<String, Object> attributes = new HashMap<>(descriptor.getAttributes());
            attributes.remove("message");
            attributes.remove("groups");
            attributes.remove("payload");

            Optional.ofNullable(fieldName).ifPresent(f -> element.put("field", f));
            element.put("type", descriptor.getAnnotation().annotationType().getSimpleName());
            element.put("attributes", attributes);
            element.put("message", message);
            element.put("invalidValue", v.getInvalidValue());
            return element;
        }
    }

    @FunctionalInterface
    interface ViolationMapper {
        Object map(ConstraintViolation v, @Nullable String fieldName, JerseyViolationException e);
    }
}
