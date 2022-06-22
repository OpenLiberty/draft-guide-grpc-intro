package io.openliberty.guides.inventory.client;

import io.openliberty.guides.systemproto.SystemServiceGrpc;
import io.openliberty.guides.systemproto.SystemServiceRequest;
import io.openliberty.guides.systemproto.SystemServiceResponse;

import java.util.Properties;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class SystemClient {

    private static final Logger logger = Logger.getLogger(SystemClient.class.getName());
    private SystemServiceGrpc.SystemServiceBlockingStub systemServiceClient;

    private void startService() {
        logger.info("about to build gRPC channel");

        // The client side gRPC code will use a gRPC ManagedChannel to connect to the
        // gRPC server
        // side service. Server side is configured to be listening on port 9080.
        // todo if 9443 remove plaintext
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9080).usePlaintext().build();

        // build the stub for client to use to make gRPC calls to the server side
        // service
        systemServiceClient = SystemServiceGrpc.newBlockingStub(channel);

        logger.info("gRPC channel initialized!");
        logger.info("channel: " + channel);
        logger.info("stub: " + systemServiceClient);
    }

    /**
     * Stop the gRPC channel
     */
    private void stopService(ManagedChannel channel) {
        channel.shutdownNow();
    }

    // @Override
    public Properties getProperties() {
        this.startService();
        // Created a protocol buffer SystemService request
        SystemServiceRequest systemRequest = SystemServiceRequest.newBuilder().build();

        // Call the RPC and get back the SystemService response (Protocol buffer)
        // Note the call is made as if it was a method from a local object

        SystemServiceResponse systemServiceResponse = systemServiceClient.getResponse(systemRequest);
        Properties props = new Properties();
        props.setProperty("os.name", systemServiceResponse.getOs());
        props.setProperty("system.load", systemServiceResponse.getSystemLoad());
        return props;

    }
}
