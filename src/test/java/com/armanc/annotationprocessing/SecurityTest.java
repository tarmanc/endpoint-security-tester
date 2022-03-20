package com.armanc.annotationprocessing;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.StringUtils;
import org.reflections.Reflections;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SecurityTest {

    private final String PUBLIC_KEY = "/public";

    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @MethodSource("getArgs")
    @DisplayName("API Privilege Test")
    void securityTest(Object clazz) {
        boolean apiSecure = true;
        String className = clazz.getClass().getName();
        String typeName = clazz.getClass().getTypeName();
        String simpleName = clazz.getClass().getSimpleName();
        Class<? extends Class> aClass = clazz.getClass().getClass();
        String message = "";

        RequestMapping classAnnotations = clazz.getClass().getAnnotation(RequestMapping.class);
        boolean secureClass = isClassSecure(clazz);
        boolean isAddressPublic = isAddressPublic(classAnnotations);
        Method[] declaredMethods = clazz.getClass().getDeclaredMethods();

        if (isAddressPublic) {
            if (!secureClass) {
                for (Method m : declaredMethods) {
                    if (isMethodSecure(m)) {
                        message = String.format("Class: %s has a public method: %s with @PreAuthorized annotation", className, m.getName());
                        apiSecure = false;
                        break;
                    }
                }
            } else {
                apiSecure = false;
                message = String.format("Class: %s can't have both @PreAuthorize annotation and public endpoint together", className);
            }
        } else {
            if (secureClass) {
                for (Method m : declaredMethods) {
                    if (isMethodAddressPublic(m)) {
                        message = String.format("@PreAuthorized Class: %s has a method: %s with public path: %s", className, m.getName(), PUBLIC_KEY);
                        apiSecure = false;
                        break;
                    }
                }
            } else {
                for (Method m : declaredMethods) {
                    boolean method = isMethodSecure(m);
                    boolean endpoint = isMethodAddressPublic(m);

                    boolean allExists = method && endpoint;
                    boolean noneExists = !method && !endpoint;
                    if (allExists || noneExists) {
                        message = String.format("Class: %s has a method: %s have @PreAuthorized annotation with public endpoint or it doesn't have neither", className, m.getName());
                        apiSecure = false;
                        break;
                    }
                }
            }
        }
        assertThat(message, apiSecure, is(true));
    }

    private boolean isMethodAddressPublic(Method method) {

        boolean getMapping = method.isAnnotationPresent(GetMapping.class);
        boolean postMapping = method.isAnnotationPresent(PostMapping.class);
        boolean deleteMapping = method.isAnnotationPresent(DeleteMapping.class);
        boolean putMapping = method.isAnnotationPresent(PutMapping.class);
        boolean patchMapping = method.isAnnotationPresent(PatchMapping.class);
        boolean requestMapping = method.isAnnotationPresent(RequestMapping.class);

        String[] path = new String[0];
        String[] value = new String[0];

        if (getMapping) {
            path = method.getAnnotation(GetMapping.class).path();
            value = method.getAnnotation(GetMapping.class).value();
            return isAddressPublic(path, value);
        } else if (postMapping) {
            path = method.getAnnotation(PostMapping.class).path();
            value = method.getAnnotation(PostMapping.class).value();
        } else if (deleteMapping) {
            path = method.getAnnotation(DeleteMapping.class).path();
            value = method.getAnnotation(DeleteMapping.class).value();
        } else if (putMapping) {
            path = method.getAnnotation(PutMapping.class).path();
            value = method.getAnnotation(PutMapping.class).value();
        } else if (patchMapping) {
            path = method.getAnnotation(PatchMapping.class).path();
            value = method.getAnnotation(PatchMapping.class).value();
        } else if (requestMapping) {
            path = method.getAnnotation(RequestMapping.class).path();
            value = method.getAnnotation(RequestMapping.class).value();
        }

        return isAddressPublic(path, value);
    }

    private boolean isAddressPublic(RequestMapping requestMapping) {
        String[] path = requestMapping.path();
        String[] value = requestMapping.value();
        return isAddressPublic(path, value);
    }

    private boolean isAddressPublic(String[] pathArr, String[] valueArr) {
        boolean isPathPublic = Arrays.stream(pathArr).anyMatch(s -> s.contains(PUBLIC_KEY));
        boolean isValuePublic = Arrays.stream(valueArr).anyMatch(s -> s.contains(PUBLIC_KEY));
        return isPathPublic || isValuePublic;
    }

    private boolean isMethodSecure(Method method) {
        if (method.isAnnotationPresent(PreAuthorize.class)) {
            PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
            String value = annotation.value();
            return !StringUtils.isBlank(value);
        }
        return false;
    }

    private boolean isClassSecure(Object clazz) {
        if (clazz.getClass().isAnnotationPresent(PreAuthorize.class)) {
            PreAuthorize annotation = clazz.getClass().getAnnotation(PreAuthorize.class);
            String value = annotation.value();
            return !StringUtils.isBlank(value);
        }
        return false;
    }

    private static Stream<Arguments> getArgs() {
        Reflections reflections = new Reflections("com.armanc.annotationprocessing");
        Set<Class<?>> annotatedClasses = new HashSet<>();
        annotatedClasses.addAll(reflections.getTypesAnnotatedWith(Controller.class));
        annotatedClasses.addAll(reflections.getTypesAnnotatedWith(RestController.class));

        return annotatedClasses.stream().map(Arguments::of);
    }
}
