package br.com.fiap.iot.NacArduino;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.fiap.iot.beans.Arduino;
import br.com.fiap.iot.beans.Sensor;
import br.com.fiap.singleton.Arduinos;

public class Listener extends Thread{
	private IMqttClient mqttClient;
	Map<Integer,Arduino>arduinos = Arduinos.instancia().getArduinos();
	public Listener(IMqttClient mqttClient2) {
		this.mqttClient=mqttClient2;
	}
	@Override
	public void run() {
		String topic= "fiap/iot/turma/2tdsg/grupo/SoundGate/devtype/esp8266/devid/all";
		try {
			mqttClient.subscribe(topic, new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
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
						listaSensores.add(sensor);
					}
					ard.setSensores(listaSensores);
					arr = obj.getJSONArray("comandos");
					for(int i=0;i<arr.length();i++) {
						listaComandos.add(arr.getJSONObject(i).getString("comando"));
					}
					ard.setComandos(listaComandos);
					if(!arduinos.containsKey(ard.getId())){
						System.out.println("novo arduino adicionado id: "+ard.getId());
					}
					arduinos.put(ard.getId(), ard);

				}
			});
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
