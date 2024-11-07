package code.configuration;

//@Configuration
public class OpenTelemetryConfig {

//  private static final AttributeKey<String> SERVICE_NAME = AttributeKey.stringKey("service.name");
//  private static final Resource MANDATORY = create(Attributes.of(SERVICE_NAME, "unknown_service:java"));

// TODO remove exporting parameter bloat with custom export config

//  @Bean
//  OpenTelemetry openTelemetry(  ) {
//    Resource resource = MANDATORY;
//    SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
//      .setResource(resource)
//      .addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder().build()).build())
//      .setSampler(Sampler.parentBased(Sampler.traceIdRatioBased(0.1)))
//      .build();
//
//
//    SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
//      .setResource(resource)
//      .registerMetricReader(PeriodicMetricReader.builder(OtlpGrpcMetricExporter.builder().build()).build())
//      .build();
//
//    return OpenTelemetrySdk.builder()
//      .setTracerProvider(sdkTracerProvider)
//      .setMeterProvider(sdkMeterProvider)
//      .buildAndRegisterGlobal();
//  }
}