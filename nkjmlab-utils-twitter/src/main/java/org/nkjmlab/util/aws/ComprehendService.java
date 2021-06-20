package org.nkjmlab.util.aws;

import org.nkjmlab.util.lang.Try;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectEntitiesRequest;
import software.amazon.awssdk.services.comprehend.model.DetectEntitiesResponse;
import software.amazon.awssdk.services.comprehend.model.DetectKeyPhrasesRequest;
import software.amazon.awssdk.services.comprehend.model.DetectKeyPhrasesResponse;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
import software.amazon.awssdk.services.comprehend.model.DetectSyntaxRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSyntaxResponse;

public class ComprehendService implements AutoCloseable {
  private final ComprehendClient client;

  public static void main(String[] args) {
    try (ComprehendService ds = new ComprehendService()) {
      String text1 = "コロナワクチンは接種したくない．";
      System.out.println(ds.detectSentiments(text1));
      System.out.println(ds.detectEntities(text1));
      System.out.println(ds.detectKeyPhrases(text1));

      String text2 = "コロナワクチンを早く接種したい．";
      System.out.println(ds.detectSentiments(text2));
      System.out.println(ds.detectEntities(text2));
      System.out.println(ds.detectKeyPhrases(text2));

    }
  }


  public ComprehendService() {
    this("default");
  }

  public ComprehendService(String profileName) {
    Region region = Region.AP_NORTHEAST_1;
    ProfileCredentialsProvider credentialsProvider =
        ProfileCredentialsProvider.builder().profileName(profileName).build();
    this.client =
        ComprehendClient.builder().credentialsProvider(credentialsProvider).region(region).build();
  }

  private DetectEntitiesResponse detectEntities(String text) {
    return detectEntities("ja", text);
  }


  public DetectSyntaxResponse detectSyntax(String text) {
    return detectSyntax("ja", text);
  }

  public DetectSyntaxResponse detectSyntax(String languageCode, String text) {
    return Try.getOrThrow(
        () -> client.detectSyntax(
            DetectSyntaxRequest.builder().text(text).languageCode(languageCode).build()),
        Try::rethrow);
  }

  public DetectKeyPhrasesResponse detectKeyPhrases(String text) {
    return detectKeyPhrases("ja", text);
  }

  public DetectKeyPhrasesResponse detectKeyPhrases(String languageCode, String text) {
    return Try.getOrThrow(
        () -> client.detectKeyPhrases(
            DetectKeyPhrasesRequest.builder().text(text).languageCode(languageCode).build()),
        Try::rethrow);
  }

  public DetectSentimentResponse detectSentiments(String text) {
    return detectSentiments("ja", text);
  }

  public DetectEntitiesResponse detectEntities(String languageCode, String text) {
    return Try.getOrThrow(
        () -> client.detectEntities(
            DetectEntitiesRequest.builder().text(text).languageCode(languageCode).build()),
        Try::rethrow);
  }

  public DetectSentimentResponse detectSentiments(String languageCode, String text) {
    return Try.getOrThrow(
        () -> client.detectSentiment(
            DetectSentimentRequest.builder().text(text).languageCode(languageCode).build()),
        Try::rethrow);
  }

  @Override
  public void close() {
    client.close();
  }



}
