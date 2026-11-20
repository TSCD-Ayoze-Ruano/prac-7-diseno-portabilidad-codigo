package software.relic;

import software.relic.adapters.DatalakeFactory;
import software.relic.domain.WebService;
import software.relic.ports.Datalake;


public class Main {
	public static void main(String[] args) {
		Datalake datalake = DatalakeFactory.create();
		WebService webService = new WebService(datalake);
		System.out.println("Servidor iniciado con el proveedor: " + datalake.getClass().getSimpleName());
	}
}
