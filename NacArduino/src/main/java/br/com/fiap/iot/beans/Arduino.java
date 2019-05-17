package br.com.fiap.iot.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Arduino implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private List<Sensor> sensores = new ArrayList<Sensor>();
	private List<String> comandos = new ArrayList<String>();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Sensor> getSensores() {
		return sensores;
	}
	public void setSensores(List<Sensor> sensores) {
		this.sensores = sensores;
	}
	public List<String> getComandos() {
		return comandos;
	}
	public void setComandos(List<String> comandos) {
		this.comandos = comandos;
	}
	public Arduino(int id, List<Sensor> sensores, List<String> comandos) {
		super();
		this.id = id;
		this.sensores = sensores;
		this.comandos = comandos;
	}
	public Arduino() {
		super();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comandos == null) ? 0 : comandos.hashCode());
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arduino other = (Arduino) obj;
		if (comandos == null) {
			if (other.comandos != null)
				return false;
		} else if (!comandos.equals(other.comandos))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
}
