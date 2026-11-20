package software.relic.adapters.aws;

import software.amazon.awssdk.annotations.NotNull;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.relic.ports.Datalake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import static software.amazon.awssdk.regions.Region.US_EAST_1;

public class S3Datalake implements Datalake {
	private static final String DEFAULT_BUCKET = "relic-production-mrw-datalake";
	private final String bucket;
	private final S3Client client;

	public S3Datalake() {
		this(s3Client(), DEFAULT_BUCKET);
	}

	public S3Datalake(String bucket) {
		this(s3Client(), bucket);
	}

	public S3Datalake(S3Client client, String bucket) {
		this.client = client;
		this.bucket = bucket;
	}

	@Override
	public String get(String id) throws Exception {
		try {
			return linesFor(id)
					.filter(line -> line.contains(id))
					.findFirst()
					.orElse(null);
		} catch (NoSuchKeyException e) {
			return null;
		}
	}

	private Stream<String> linesFor(String id) {
		return linesFor(requestIn(id));
	}

	private Stream<String> linesFor(GetObjectRequest request) {
		return linesFor(client.getObject(request));
	}

	private Stream<String> linesFor(InputStream is) {
		return linesFor(toReader(is)).onClose(() -> close(is));
	}

	private Stream<String> linesFor(BufferedReader reader) {
		return reader.lines();
	}

	private GetObjectRequest requestIn(String id) {
		return GetObjectRequest.builder()
				.bucket(bucket)
				.key(keyIn(id))
				.build();
	}

	private static String keyIn(String id) {
		return id.substring(0, 8) + ".tsv";
	}

	private static BufferedReader toReader(InputStream input) {
		return new BufferedReader(new InputStreamReader(input));
	}

	private static void close(InputStream is) {
		try {
			is.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static S3Client s3Client() {
		return S3Client.builder()
				.region(US_EAST_1)
				.credentialsProvider(credentialsProvider())
				.build();
	}

	@NotNull
	private static AwsCredentialsProvider credentialsProvider() {
		return DefaultCredentialsProvider.builder().build();
	}

	@Override
	public void close() {
		client.close();
	}
}
