package io.openliberty.guides.systemproto;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.openliberty.guides.systemproto.SystemServiceGrpc;
import io.openliberty.guides.systemproto.SystemServiceRequest;
import io.openliberty.guides.systemproto.SystemServiceResponse;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.openliberty.guides.systemproto.SystemLoad;
import io.reactivex.rxjava3.core.Flowable;
import org.reactivestreams.Publisher;


public class SystemService extends SystemServiceGrpc.SystemServiceImplBase {

    // required public zero-arg constructor
    public SystemService() {
    }

    // /**
    //  * Unary RPC example
    //  */
    // @Override
    // public void getSystem(SystemServiceRequest request, StreamObserver<SystemServiceResponse> responseObserver) {
    //     String os = System.getProperty("os.name");
    //     String javaVersion = System.getProperty("java.version");
    //     // String name = request

    //     // Create response
    //     SystemServiceResponse response = SystemServiceResponse.newBuilder().setOs(os).setJavaVersion(javaVersion)
    //             .build();

    //     // Send the response
    //     responseObserver.onNext(response);

    //     // Complete the RPC call
    //     responseObserver.onCompleted();
    // }

    // @Override
    // public void getMessage(SystemServiceRequest request, StreamObserver<SystemServiceResponse> responseObserver) {
    //     StringBuilder builder = new StringBuilder();
    //     builder.append("Hello");

    //     SystemServiceResponse response = SystemServiceResponse.newBuilder().setMessage(builder.toString()).build();

    //     responseObserver.onNext(response);

    //     responseObserver.onCompleted();

    // }

    @Inject
    @ConfigProperty(name="UPDATE_INTERVAL", defaultValue="5")
    private long updateInterval;

    private static final OperatingSystemMXBean osMean = 
            ManagementFactory.getOperatingSystemMXBean();

    private static String hostname = null;

    private static String getHostname() {
        if (hostname == null) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                hostname = System.getenv("HOSTNAME");
            }
        }
        return hostname;
    }

    @Override
    public void getResponse(SystemServiceRequest request, StreamObserver<SystemServiceResponse> responseObserver) {
        String os = System.getProperty("os.name");
        String systemLoad = Flowable.interval(updateInterval, TimeUnit.SECONDS).map((interval -> new SystemLoad(getHostname(), Double.valueOf(osMean.getSystemLoadAverage())))).toString();

        // Create response
        SystemServiceResponse response = SystemServiceResponse.newBuilder().setOs(os).setSystemLoad(systemLoad)
                .build();

        // Send the response
        responseObserver.onNext(response);

        // Complete the RPC call
        responseObserver.onCompleted();
    }

}
