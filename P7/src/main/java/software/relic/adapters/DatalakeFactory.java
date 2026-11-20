package software.relic.adapters;

import software.relic.adapters.aws.S3Datalake;
import software.relic.adapters.fake.FakeDatalake;
import software.relic.adapters.local.LocalFilesystemDatalake;
import software.relic.ports.Datalake;

import java.nio.file.Paths;

public class DatalakeFactory {
    public static Datalake create() {
        String provider = System.getenv().getOrDefault("DATALAKE_PROVIDER", "local").toLowerCase();

        return switch (provider) {
            case "s3" -> {
                String bucket = System.getenv().getOrDefault("DATALAKE_BUCKET", "relic-production-mrw-datalake");
                yield new S3Datalake(bucket);
            }
            case "fake" -> new FakeDatalake();
            case "local" -> {
                String path = System.getenv().getOrDefault("DATALAKE_PATH", "data");
                yield new LocalFilesystemDatalake(Paths.get(path));
            }
            default -> throw new IllegalArgumentException("Proveedor no soportado: " + provider);
        };
    }
}
