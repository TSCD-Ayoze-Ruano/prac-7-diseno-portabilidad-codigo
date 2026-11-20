package software.relic.adapters.fake;

import software.relic.ports.Datalake;

import java.util.HashMap;
import java.util.Map;

public class FakeDatalake implements Datalake {
	private final Map<String, String> data;

	public FakeDatalake() {
		this.data = new HashMap<>();
	}

	public FakeDatalake(Map<String, String> initialData) {
		this.data = new HashMap<>(initialData);
	}

	public void put(String id, String content) {
		this.data.put(id, content);
	}

	@Override
	public String get(String id) throws Exception {
		return data.get(id);
	}

	@Override
	public void close() {
		// No requiere liberar recursos externos en memoria
	}
}
