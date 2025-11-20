package org.example.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Application класс для активации JAX-RS REST API.
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // Пустой класс - JAX-RS автоматически сканирует аннотации @Path
}

