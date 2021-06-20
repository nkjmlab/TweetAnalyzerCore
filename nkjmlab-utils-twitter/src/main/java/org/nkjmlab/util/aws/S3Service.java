package org.nkjmlab.util.aws;


import java.io.IOException;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


public class S3Service implements AutoCloseable {
  private static org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();

  private final Region region = Region.AP_NORTHEAST_1;
  private final S3Client client;


  public static void main(String[] args) throws IOException {
    try (S3Service s3 = new S3Service()) {
      s3.putBucket();
    }
  }

  public S3Service() {
    this("default");
  }

  public S3Service(String profileName) {
    ProfileCredentialsProvider credentialsProvider =
        ProfileCredentialsProvider.builder().profileName(profileName).build();
    client = S3Client.builder().credentialsProvider(credentialsProvider).region(region).build();
  }

  private void putBucket() {
    String bucketName = "bucket" + System.currentTimeMillis();
    String keyName = "key";

    createBacket(bucketName);
    putObject(bucketName, keyName);

    log.info("Cleaning up...");
    deleteObject(bucketName, keyName);
    deleteBucket(bucketName);
    log.info("Cleanup complete");

    close();

  }

  @Override
  public void close() {
    log.info("Closing the connection to Amazon S3");
    client.close();
    log.info("Connection closed");
    log.info("Exiting...");
  }

  public void createBacket(String bucketName) {
    log.info("Creating bucket: " + bucketName);
    client
        .createBucket(CreateBucketRequest.builder().bucket(bucketName)
            .createBucketConfiguration(
                CreateBucketConfiguration.builder().locationConstraint(region.id()).build())
            .build());
    client.waiter().waitUntilBucketExists(HeadBucketRequest.builder().bucket(bucketName).build());
    log.info(bucketName + " is ready.");
  }

  public void deleteBucket(String bucketName) {
    log.info("Deleting bucket: " + bucketName);
    client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
    log.info(bucketName + " has been deleted.");

  }

  public void putObject(String bucketName, String keyName) {
    log.info("Uploading object...");

    client.putObject(PutObjectRequest.builder().bucket(bucketName).key(keyName).build(),
        RequestBody.fromString("Testing with the AWS SDK for Java"));

    log.info("Upload complete");

  }

  public void deleteObject(String bucketName, String keyName) {
    log.info("Deleting object: " + keyName);
    client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build());
    log.info(keyName + " has been deleted.");
  }
}
