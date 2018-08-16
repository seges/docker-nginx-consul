package ru.zhenik.akka.example;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import ru.zhenik.akka.example.infrastructure.discovery.DiscoveryAgentActor;
import ru.zhenik.akka.example.interfaces.rest.AppResource;

public class Boot {

  public static void main(String[] args) {
    // config
    final String appId = UUID.randomUUID().toString();
    final AppConfiguration appConfig = AppConfiguration.loadConfig(appId);

    // actor system init
    final ActorSystem system = ActorSystem.create();
    final Materializer materializer = ActorMaterializer.create(system);

    // service discovery actor
    final ActorRef serviceDiscoveryActor = system.actorOf(DiscoveryAgentActor.props(appConfig), "example-app-consul-service");

    // http init
    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = new AppResource(appConfig).routes().flow(system, materializer);
    final CompletionStage<ServerBinding> binding = Http
        .get(system)
        .bindAndHandle(
            routeFlow,
            ConnectHttp.toHost(appConfig.host, appConfig.port),
            materializer
        );

    // exception handling
    binding.exceptionally(failure -> {
      System.err.println("Something very bad happened! " + failure.getMessage());
      system.terminate();
      return null;
    });

  }
}
