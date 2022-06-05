package io.openliberty.guides.systemproto;

import java.util.Properties;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.openliberty.guides.systemproto.SystemServiceGrpc;
import io.openliberty.guides.systemproto.SystemServiceRequest;
import io.openliberty.guides.systemproto.SystemServiceResponse;

public class SystemService extends SystemServiceGrpc.SystemServiceImplBase {

    // required public zero-arg constructor
    public SystemService() {
    }

    /**
     * Unary RPC example
     */
    @Override
    public void getSystem(SystemServiceRequest request, StreamObserver<SystemServiceResponse> responseObserver) {
        String os = System.getProperty("os.name");
        String javaVersion = System.getProperty("java.version");
        // String name = request

        // Create response
        SystemServiceResponse response = SystemServiceResponse.newBuilder().setOs(os).setJavaVersion(javaVersion)
                .build();

        // Send the response
        responseObserver.onNext(response);

        // Complete the RPC call
        responseObserver.onCompleted();
    }
}