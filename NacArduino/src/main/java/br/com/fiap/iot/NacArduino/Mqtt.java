package br.com.fiap.iot.NacArduino;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import br.com.fiap.iot.beans.Arduino;
import br.com.fiap.iot.beans.Sensor;
import br.com.fiap.singleton.Arduinos;

@Path("Mqtt")
@WebListener
public class Mqtt implements ServletContextListener{
	private static final Map<Integer,Arduino> arduinos= Arduinos.instancia().getArduinos();
	private IMqttClient mqttClient; 
	Listener a =null;
	//private String lastMessage=null;
	Timer time = new Timer();
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			arduinos.clear();
			System.out.println("Lista atualizada");
		}

	};

	public Mqtt() throws MqttException {
		try {
			String url = "tcp://broker.hivemq.com:1883";
			String clientId = UUID.randomUUID().toString();
			//Por padrão o Paho usa persistência em disco,
			//mas pode ter problema com permissão quando usado em um Webservice
			MqttClientPersistence persist = new MemoryPersistence();
			mqttClient = new MqttClient(url, clientId, persist);
			MqttConnectOptions options = new MqttConnectOptions();

			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(100);

			mqttClient.connect(options);
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}
	}

	@POST @Path("fiap/iot/turma/2tdsg/grupo/SoundGate/devtype/esp8266/devid/{id}/cmd/{topic}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getCliente( String message,@PathParam("id") String id,@PathParam("topic")String topico) throws MqttPersistenceException, MqttException {	
		String topic = "fiap/iot/turma/2tdsg/grupo/SoundGate/devtype/esp8266/devid/"+id+"/cmd/"+topico;
		if(mqttClient.isConnected()) {
			MqttMessage msg = new MqttMessage(message.getBytes());
			msg.setQos(0);
			msg.setRetained(false);
			mqttClient.publish(topic,msg);
			return "Message sent to topic: "+ topic;
		} else return "Client not connected!";
	}
	/*public void objeto() throws MqttSecurityException, MqttException, InterruptedException {
			String topic= "fiap/iot/turma/2tdsg/grupo/grupo/devtype/arduino/devid/all";
			mqttClient.subscribe(topic, new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					System.out.println("Algo chegou");
					JSONObject obj = new JSONObject(message.toString());
					Arduino ard=new Arduino();
				List<Sensor> listaSensores=new ArrayList<Sensor>();
				List<String> listaComandos=new ArrayList<String>();
				ard.setId(obj.getInt("id"));
				JSONArray arr = obj.getJSONArray("sensores");
				for(int i=0;i<arr.length();i++) {
					Sensor sensor=new Sensor();
					sensor.setNome(arr.getJSONObject(i).getString("nome"));
					sensor.setLeitura(arr.getJSONObject(i).getString("leitura"));
					sensor.setStatus(arr.getJSONObject(i).getBoolean("status"));
					listaSensores.add(sensor);
				}
				ard.setSensores(listaSensores);
				arr = obj.getJSONArray("comandos");
				for(int i=0;i<arr.length();i++) {
					listaComandos.add(arr.getJSONObject(i).getString("comando"));
				}
				ard.setComandos(listaComandos);
				ard.setStatus(obj.getBoolean("status"));
				if(arduinos.contains(ard))
					arduinos.remove(ard);
				arduinos.add(ard);
				System.out.println(ard.getSensores().size());
				System.out.println(arduinos.size());

			}
		});
	}Metodo antigo */
	@GET
	@Path("fiap/iot/turma/2tdsg/grupo/SoundGate/devtype/esp8266/devid/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> ids(){
		List<Integer> listaId=new ArrayList<Integer>();
		for(int a:arduinos.keySet()) {
			listaId.add(a);
		}
		return listaId;
	}
	@GET
	@Path("fiap/iot/turma/2tdsg/grupo/SoundGate/devtype/esp8266/devid/{id}/cmd/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> comandosDisponisveis(@PathParam("id") int id){
		Arduino a = pegarArduinoPorId(id);
		if(a!=null)
			return a.getComandos();
		return null;
	}
	@GET
	@Path("fiap/iot/turma/2tdsg/grupo/SoundGate/devtype/esp8266/devid/{id}/sensor/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Sensor> sensoresDisponiveis(@PathParam("id") int id){
		Arduino a = pegarArduinoPorId(id);
		if(a!=null)
			return a.getSensores();
		return null;
	}
	public Arduino pegarArduinoPorId(int id) {
		return arduinos.get(id);
	}
	/*@GET @Path("waitmessage/{topic}")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getMessage(@PathParam("topic") String topic) throws MqttSecurityException, MqttException, InterruptedException {	
		lastMessage = null;
		mqttClient.subscribe(topic.replace('_', '/'), new IMqttMessageListener() {
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				synchronized (Mqtt.this) {
					lastMessage = message.toString();
					System.out.println(lastMessage);
					Mqtt.this.notifyAll();
				}
			}
		});
		this.wait();
		return lastMessage;
	}*/

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("ligou");
		a=new Listener(mqttClient);
		a.start();
		System.out.println("listener chamado");
		time.scheduleAtFixedRate(task, 0, 60000);
		System.out.println("timer chamado");
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		time.cancel();
		a.interrupt();
	}

}
