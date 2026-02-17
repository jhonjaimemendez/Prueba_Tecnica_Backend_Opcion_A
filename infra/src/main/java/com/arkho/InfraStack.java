package com.arkho;

import java.util.List;
import java.util.Map;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketEncryption;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class InfraStack extends Stack {

    public InfraStack(final Construct scope, final String id) {
        super(scope, id);

        Vpc vpc = Vpc.Builder
                .create(this, "GestionFlotasVpc")
                .maxAzs(2)
                .build();

        
        Bucket bucket = Bucket.Builder
                .create(this, "UploadsBucket")
                .bucketName("gestionflotas-uploads-jmendez")
                .blockPublicAccess(BlockPublicAccess.BLOCK_ALL)
                .encryption(BucketEncryption.S3_MANAGED)
                .build();

        
        Queue queue = Queue.Builder
                .create(this, "SolicitudQueue")
                .queueName("gestionflotas-solicitudes")
                .visibilityTimeout(Duration.seconds(30))
                .build();

        
        DatabaseInstance db = DatabaseInstance.Builder
                .create(this, "GestionFlotasDB")
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_15)
                                .build()))
                .vpc(vpc)
                .credentials(Credentials.fromGeneratedSecret("postgres"))
                .instanceType(
                        software.amazon.awscdk.services.ec2.InstanceType.of(
                                software.amazon.awscdk.services.ec2.InstanceClass.BURSTABLE3,
                                software.amazon.awscdk.services.ec2.InstanceSize.MICRO
                        )
                )
                .allocatedStorage(20)
                .build();

     
        Cluster cluster = Cluster.Builder
                .create(this, "GestionFlotasCluster")
                .vpc(vpc)
                .build();

        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                        .create(this, "TaskDef")
                        .memoryLimitMiB(512)
                        .cpu(256)
                        .build();

        taskDefinition.addContainer("AppContainer",
                ContainerDefinitionOptions.builder()
                        .image(ContainerImage.fromAsset("../backend/Gestionflotas/deploy"))
                        .portMappings(List.of(
                                PortMapping.builder()
                                        .containerPort(8080)
                                        .build()))
                        .environment(Map.of(
                                "AWS_SQS_QUEUE_URL", queue.getQueueUrl(),
                                "AWS_S3_BUCKET", bucket.getBucketName()
                        ))
                        .build());

        FargateService service = FargateService.Builder
                .create(this, "Service")
                .cluster(cluster)
                .taskDefinition(taskDefinition)
                .desiredCount(1)
                .build();

        
        bucket.grantPut(taskDefinition.getTaskRole());
        queue.grantSendMessages(taskDefinition.getTaskRole());
    }
}
