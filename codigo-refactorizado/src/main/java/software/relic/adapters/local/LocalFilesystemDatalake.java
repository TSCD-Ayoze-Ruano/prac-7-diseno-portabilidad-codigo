package software.relic.adapters.local;

import software.relic.ports.Datalake;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LocalFilesystemDatalake implements Datalake {
	private static final Path DEFAULT_BASE_PATH = Paths.get("data");
	private final Path basePath;

	public LocalFilesystemDatalake() {
		this(DEFAULT_BASE_PATH);
	}

	public LocalFilesystemDatalake(Path basePath) {
		this.basePath = basePath;
	}

	@Override
	public String get(String id) throws Exception {
		Path directPath = resolveFilePath(id);
		if (Files.exists(directPath)) {
			try (Stream<String> lines = Files.lines(directPath)) {
				return lines.filter(line -> line.contains(id)).findFirst().orElse(null);
			}
		}

		// Fallback: si no existe id.substring(0, 8) + ".tsv", busca en los ficheros .tsv de la carpeta data
		if (Files.exists(basePath) && Files.isDirectory(basePath)) {
			try (Stream<Path> files = Files.list(basePath)) {
				for (Path file : files.filter(p -> p.toString().endsWith(".tsv")).toList()) {
					try (Stream<String> lines = Files.lines(file)) {
						var match = lines.filter(line -> line.contains(id)).findFirst();
						if (match.isPresent()) {
							return match.get();
						}
					}
				}
			}
		}

		return null;
	}

	private Path resolveFilePath(String id) {
		String filename = id.length() >= 8 ? id.substring(0, 8) + ".tsv" : id + ".tsv";
		return basePath.resolve(filename);
	}

	@Override
	public void close() {
		// No hay recursos externos o conexiones que liberar en el sistema local
	}
}
