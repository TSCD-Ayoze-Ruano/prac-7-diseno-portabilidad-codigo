package software.relic.ports;

public interface Datalake extends AutoCloseable {
	String get(String id) throws Exception;

	@Override
	void close();
}
