package software.relic.domain;

import io.javalin.Javalin;
import io.javalin.http.Context;
import software.relic.ports.Datalake;

import java.io.Closeable;

public class WebService implements Closeable {
	private static final int DEFAULT_PORT = 7000;
	private final Javalin service;
	private final Datalake datalake;

	public WebService(Datalake datalake) {
		this(datalake, DEFAULT_PORT);
	}

	public WebService(Datalake datalake, int port) {
		this.service = Javalin.create().start(port);
		this.datalake = datalake;
		init();
	}

	public int port() {
		return service.port();
	}

	public void init() {
		service.get("/sales/{id}", this::retrieveSale);
	}

	private void retrieveSale(Context ctx) {
		String id = ctx.pathParam("id");

		try {
			String json = datalake.get(id);
			if (json != null) {
				ctx.result(json);
			} else {
				ctx.status(404).result("Sale not found: " + id);
			}
		} catch (Exception e) {
			ctx.status(500).result("Error retrieving sale: " + e.getMessage());
		}
	}

	@Override
	public void close() {
		service.stop();
		datalake.close();
	}
}
