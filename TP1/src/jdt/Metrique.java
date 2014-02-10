package jdt;

public class Metrique {
	
	private String nomClasse;
	private int LOC;
	private int NbreAttrImpl;
	private int NbreAttrPub;
	private int NbreInterfaces;
	private float PctAttrPub;
	private float PctAttrHer;
	private int NbreMethImpl;
	private float PctMethPub;
	private float PctMethHer;
	private int DIT;
	private int NOC;
	private int WMC;
	private int LCOM;
	private int CBO;
	private int fanOut;
	private int fanIn;
	
	public String toString()
	{
		return nomClasse + ";" + LOC+ ";" +NbreInterfaces+ ";" +NbreAttrPub+ ";" +NbreAttrImpl+ ";" +PctAttrPub+ ";" +PctAttrHer+ ";" +NbreMethImpl+ ";" +PctMethPub+ ";" +PctMethHer+ ";" +DIT+ ";" +NOC+ ";" +WMC+ ";" +LCOM+ ";" +CBO+ ";" +fanOut+ ";" +fanIn;
		
	}
	
	public void setNbreAttrImpl(int nbreAttrImpl) {
		this.NbreAttrImpl = nbreAttrImpl;
	}

	public void setNomClasse(String nomClasse) {
		this.nomClasse = nomClasse;
	}
	public void setLOC(int lOC) {
		LOC = lOC;
	}
	public void setNbreAttrPub(int nbreAttrPub) {
		NbreAttrPub = nbreAttrPub;
	}
	public void setNbreInterfaces(int nbreInterfaces) {
		NbreInterfaces = nbreInterfaces;
	}
	public void setPctAttrPub(float pctAttrPub) {
		PctAttrPub = pctAttrPub;
	}
	public void setPctAttrHer(float pctAttrHer) {
		PctAttrHer = pctAttrHer;
	}
	public void setNbreMethImpl(int nbreMethImpl) {
		NbreMethImpl = nbreMethImpl;
	}
	public void setPctMethPub(float pctMethPub) {
		PctMethPub = pctMethPub;
	}
	public void setPctMethHer(float pctMethHer) {
		PctMethHer = pctMethHer;
	}
	public void setDIT(int dIT) {
		DIT = dIT;
	}
	public void setNOC(int nOC) {
		NOC = nOC;
	}
	public void setWMC(int wMC) {
		WMC = wMC;
	}
	public void setLCOM(int lCOM) {
		LCOM = lCOM;
	}
	public void setCBO(int cBO) {
		CBO = cBO;
	}
	public void setFanOut(int fanOut) {
		this.fanOut = fanOut;
	}
	public void setFanIn(int fanIn) {
		this.fanIn = fanIn;
	}

}
