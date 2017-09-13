package smpp4j.test.ussd;

import smpp4j.ussd.UssdMessage;
import smpp4j.ussd.UssdSession;
import smpp4j.ussd.UssdMessage.R;

public class MiSession extends UssdSession {
	
	private boolean isInWhiteList() {
		return msisdn.matches("(595111111)");
	}

	public MiSession(String msisdn) {
		super(msisdn);
		
/*
Si es desde el *111#
====================
Filtrar ofertas por suscripcion,
si se muestran las ofertas si o si y dp se validan si se pueden usar, mostrar todas las ofertas, al seleccionar una oferta se corren sus validadores.
correr los validadores por oferta suscripcion
armar lista de ofertas,
si se selecciono una oferta, accion va a ser alta por defecto,
	se le piden los keys definidos en los tipos de beneficio extras
	se le pide confirmacion,
si se pide listar. se llama a un ws pasando msisdn y cod suscripcion para que envie lista de ofertas.
si se pide eliminar. se busca oferta activa dentro de suscripcion y se llama al manager con accion eliminar 
*/
		// averiguar todas las validacioens...
		// 
		
		setNextReceiver(iniMenu);
	}
	
	private final R iniMenu = new R() { public UssdMessage onReceive(String message) {
//			if (!isInWhiteList())
//				return new UssdMessage(".");
			// return  UssdMessage.new(".");
			
			return new UssdMessage("1. Paquetigos\n2. SinSaldo?", 
					1, paquetigoMenu, 
					2, sinSaldoMenu ); } };

	private final R paquetigoMenu = new R() {
		public UssdMessage onReceive(String message) {
			return new UssdMessage("1. SubMenu1\n2. Pqt final?", 
					1, paquetigoMenu_submenu1, 
					2, paquetigoMenu_pqtFinal ); } };

	private final R paquetigoMenu_submenu1 = new R() {
		public UssdMessage onReceive(String message) {
			
			return new UssdMessage("1. Final1\n2. Final2",
					1, paquetigoMenu_submenu1_final1, 
					2, paquetigoMenu_submenu1_final2 ); } };
					
	private final R paquetigoMenu_submenu1_final1 = new R() {
		public UssdMessage onReceive(String message) {
			return new UssdMessage("final 1", new Object[] {null}); } };
			
	private final R paquetigoMenu_submenu1_final2 = new R() {
		public UssdMessage onReceive(String message) {
			return new UssdMessage("final 2"); } };

	private final R paquetigoMenu_pqtFinal = new R() {
		public UssdMessage onReceive(String message) {
			return new UssdMessage("llegaste hasta aca leka"); } };

	private final R sinSaldoMenu = new R() {
		public UssdMessage onReceive(String message) {
			return new UssdMessage("nde sogue"); } };

	public void cleanUpSession() {
		
	}
}

