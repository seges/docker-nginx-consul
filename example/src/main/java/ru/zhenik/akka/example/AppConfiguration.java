package ru.zhenik.akka.example;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AppConfiguration {
  public final String appId;
  public final String serviceName;
  public final String host;
  public final Integer port;
  public final ServiceDiscoveryConfiguration serviceDiscoveryConfiguration;

  private AppConfiguration(final String appId){
    // load application.conf file
    Config config = ConfigFactory.load();

    this.appId = appId;
    this.serviceName = config.getString("service.name");
    this.host = config.getString("application.host");
    this.port = config.getInt("application.port");
    this.serviceDiscoveryConfiguration =
        new ServiceDiscoveryConfiguration(
            config.getString("discovery.host"),
            config.getInt("discovery.port"),
            config.getLong("discovery.healthcheck-timeout")
        );

  }

  public static AppConfiguration loadConfig(final String appId) {
    return new AppConfiguration(appId);
  }

  public final static class ServiceDiscoveryConfiguration {
    public final String host;
    public final int port;
    public final long healthCheckTimeout;
    private ServiceDiscoveryConfiguration(String host, int port, long healthCheckTimeout) {
      this.host = host;
      this.port = port;
      this.healthCheckTimeout = healthCheckTimeout;
    }
  }
}
