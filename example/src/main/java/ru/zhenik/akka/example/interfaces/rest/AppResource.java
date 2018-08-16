package ru.zhenik.akka.example.interfaces.rest;

import static akka.http.javadsl.server.Directives.complete;
import static akka.http.javadsl.server.Directives.get;
import static akka.http.javadsl.server.Directives.path;
import static akka.http.javadsl.server.Directives.post;
import static akka.http.javadsl.server.Directives.route;

import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import ru.zhenik.akka.example.AppConfiguration;

public final class AppResource {

  public final AppConfiguration config;
  public final String healthResponse;

  public AppResource(AppConfiguration config) {
    this.config = config;
    this.healthResponse = String
        .format("ok \nservice-name:[%s]\napp-id:[%s]\nhost&port:[%s]", config.serviceName,
            config.appId, config.host + ":" + config.port);
  }

  public Route routes() {

    Route managementRoutes = path("health", () ->
        get(() ->
            complete(HttpEntities.create(ContentTypes.TEXT_PLAIN_UTF8, healthResponse))
        )
    );
    Route appRoutes = path("app", () ->
        post(() ->
            complete(StatusCodes.OK)
        )
    );

    return route(managementRoutes, appRoutes);

  }
}