package io.openliberty.guides.greeter;

import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;

import io.grpc.stub.StreamObserver;
import io.openliberty.guides.greeter.GreeterServiceInterceptor;
import io.openliberty.sample.GreetingRequest;
import io.openliberty.sample.GreetingResponse;
import io.openliberty.sample.SampleServiceGrpc;

/**
 * Example of a gRPC service which can be deployed on Liberty. This service will return a simple greeting string.
 */
public class GreeterService extends SampleServiceGrpc.SampleServiceImplBase {

    private static final Logger logger = Logger.getLogger(GreeterService.class.getName());

    // required public zero-arg constructor
    public GreeterService(){}

    /**
     * Unary RPC example
     */
    @Override
    @RolesAllowed("Role1")
    public void getGreeting(GreetingRequest req, StreamObserver<GreetingResponse> resp) {
        StringBuilder builder = new StringBuilder();
        builder.append("Hello ");
        builder.append(req.getName());

        // get the context value set for GreeterClientInterceptor by GreeterServiceInterceptor
        // and add it to the response
        Object clientHostFromContext = GreeterServiceInterceptor.CLIENT_HOST_KEY.get();
        if (clientHostFromContext != null) {
            builder.append(" (");
            builder.append(clientHostFromContext.toString());
            builder.append(")");
        }

        // get the context value set by GreeterServiceInterceptor
        // and add it to the response
        Object timeFromContext = GreeterServiceInterceptor.CURRENT_TIME_KEY.get();
        if (timeFromContext != null) {
            builder.append("; the current time is ");
            builder.append(timeFromContext.toString());
        }

        // return the greeting to the client
        GreetingResponse responseString = GreetingResponse.newBuilder().setMessage(builder.toString()).build();
        resp.onNext(responseString);
        resp.onCompleted();
    }

    /**
     * Server Streaming RPC example
     */
    @Override
    @RolesAllowed({"Role1", "Role2"})
    public void getServerStreamingGreeting(GreetingRequest req, StreamObserver<GreetingResponse> resp) {
        StringBuilder builder = new StringBuilder();
        builder.append("Hello ");
        builder.append(req.getName());

        // stream the first response message containing the given name
        GreetingResponse responseString = GreetingResponse.newBuilder().setMessage(builder.toString()).build();
        resp.onNext(responseString);

        // stream a second response message
        responseString = GreetingResponse.newBuilder().setMessage("Have a nice day").build();
        resp.onNext(responseString);

        resp.onCompleted();
    }

}
