package Modelo;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Modelo.EstadoJuego") // another mapping
public class EstadoJuego {
	@XStreamImplicit(itemFieldName = "Cristobal")
	private PersonajePrincipal Cristobal = new PersonajePrincipal();
	@XStreamImplicit(itemFieldName = "Hermana")
	private PersonajePrincipal Hermana = new PersonajePrincipal();
	@XStreamImplicit(itemFieldName = "Mapa")
	private Mapa Mapa;
	public PersonajePrincipal getCristobal() {
		return Cristobal;
	}
	public void setCristobal(PersonajePrincipal cristobal) {
		Cristobal = cristobal;
	}

	public PersonajePrincipal getHermana() {
		return Hermana;
	}
	public void setHermana(PersonajePrincipal hermana) {
		Hermana = hermana;
	}
	
	public Mapa getMapa() {
		return Mapa;
	}
	
	public void setMapa(Mapa mapa) {
		Mapa = mapa;
	}
}
