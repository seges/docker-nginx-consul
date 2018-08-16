package ru.zhenik.akka.example.infrastructure.discovery;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import ru.zhenik.akka.example.AppConfiguration;
import scala.concurrent.duration.FiniteDuration;

/**
 *
 * */
public class DiscoveryAgentActor extends AbstractActor {

  public static Props props(AppConfiguration config) {
    return Props.create(DiscoveryAgentActor.class, () -> new DiscoveryAgentActor(config));
  }

  private final AppConfiguration configuration;
  private final Consul consul;
  private final AgentClient agentClient;
  private final FiniteDuration SCHEDULED_WORK_DELAY;

  public DiscoveryAgentActor(AppConfiguration configuration){
    this.configuration = configuration;
    this.SCHEDULED_WORK_DELAY =  new FiniteDuration(configuration.serviceDiscoveryConfiguration.healthCheckTimeout, TimeUnit.SECONDS);

    // todo: terminate system if error occur while connecting to consul
    // get consul connection
    this.consul = Consul
        .builder()
        .withHostAndPort(
            HostAndPort.fromParts(
                configuration.serviceDiscoveryConfiguration.host,
                configuration.serviceDiscoveryConfiguration.port)
        )
        .build();

    // get agent
    agentClient = consul.agentClient();
    // set registration config
    Registration service = ImmutableRegistration.builder()
        .id(configuration.appId)
        .name(configuration.serviceName)
        .port(configuration.port)
        .address(configuration.host)
        .check(Registration.RegCheck.ttl(configuration.serviceDiscoveryConfiguration.healthCheckTimeout))
        .tags(Collections.singletonList("tag1"))
        .meta(Collections.singletonMap("version", "1.0"))
        .build();

    // register service
    agentClient.register(service);
    // check in with Consul, serviceId required only.  client will prepend "service:" for service level checks.
    // Note that you need to continually check in before the TTL expires, otherwise your service's state will be marked as "critical".
  }

  @Override
  public void preStart() {
    getSelf().tell("Do Scheduled Work", ActorRef.noSender());
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .matchEquals("Do Scheduled Work", work -> {
          sendHealthCheck();
          context().system()
              // send each (10seconds default) health-check to consul
              .scheduler()
              .schedule(
                  // delay before 1st request
                  new FiniteDuration(5, TimeUnit.SECONDS),
                  SCHEDULED_WORK_DELAY,
                  healthCheck(),
                  getContext().dispatcher()
              );
        })
        .build();
  }

  private void sendHealthCheck() {
    try {
      agentClient.pass(configuration.appId, configuration.serviceName +" alive and reachable");
    } catch (NotRegisteredException e) {
      e.printStackTrace();
      getContext().getSystem().terminate();
    }
    System.out.println("Health check has been sent");
  }

  private Runnable healthCheck() {
    return this::sendHealthCheck;
  }

}
