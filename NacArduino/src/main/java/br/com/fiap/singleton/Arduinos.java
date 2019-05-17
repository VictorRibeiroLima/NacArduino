package br.com.fiap.singleton;

import java.util.HashMap;
import java.util.Map;

import br.com.fiap.iot.beans.Arduino;

public class Arduinos {
	private static Arduinos ard =null;
	private static final Map<Integer,Arduino> arduinos = new HashMap<Integer,Arduino>();
	private Arduinos() {
		
	}
	public static Arduinos instancia() {
		if(ard==null) {
			ard=new Arduinos();
		}
		return ard;
	}
	public Map<Integer,Arduino> getArduinos(){
		return arduinos;
	}
}
