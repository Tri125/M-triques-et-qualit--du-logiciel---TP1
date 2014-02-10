   /****************
    * 
   TP1 IFT3913
   --Tristan Savaria 1062837
             et
   --IBRAHIMA FAYE 959315
   *
   *
   *****************/
package jdt;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Metrique {
	
	private  String nomClasse;
	private  int LOC;
	private  int NbreAttrImpl;
	private  int NbreInterfaces;
	private  float PctAttrPub;
	private  float PctAttrHer;
	private  int NbreMethImpl;
	private  float PctMethPub;
	private  float PctMethHer;
	private  int DIT;
	private  Map<String, Integer> NOCmap;
	private  int NOC;
	private  int WMC;
	private  int LCOM;
	private  int CBO;
	private  int fanOut;
	private  int fanIn;
    private DecimalFormat df = new DecimalFormat();
	
	public String toString()
	{
	    df.setMaximumFractionDigits(2);
	    
		String enTete = "Classe;LOC;NbreInterfaces;NbreAttrImpl;PctAttrPub;PctAttrHer;NbreMethImpl;PctMethPub;PctMethHer;DIT;NOC;WMC;LCOM;RFC;CBO;fanOut;fanIn\n";
		
		return enTete+ nomClasse + ";" + LOC+ ";" +NbreInterfaces+ ";" +NbreAttrImpl+ ";" +	df.format(PctAttrPub) + "%"+ ";"  +	df.format(PctAttrHer) + "%"+ ";" +NbreMethImpl+ ";" +	df.format(PctMethPub) + "%"+ ";" 
		+	df.format(PctMethHer) + "%"+ ";" +DIT+ ";" +NOC+ ";" +WMC+ ";" +"LCOM NotImplemented"+ ";" +" RFC NotRequested"+ "CBO NotImplemented"+ ";" +fanOut+ ";" +"fanIn NotImplemented";
		
	}
	
	public  void setNbreAttrImpl(int nbreAttrImpl1) {
		NbreAttrImpl = nbreAttrImpl1;
	}

	public  void setNomClasse(String nomClasse1) {
		nomClasse = nomClasse1;
	}
	public   void setLOC(int lOC) {
		LOC = lOC;
	}

	public   void setNbreInterfaces(int nbreInterfaces) {
		NbreInterfaces = nbreInterfaces;
	}
	public   void setPctAttrPub(float pctAttrPub) {
		PctAttrPub = pctAttrPub;
	}
	public   void setPctAttrHer(float pctAttrHer) {
		PctAttrHer = pctAttrHer;
	}
	public   void setNbreMethImpl(int nbreMethImpl) {
		NbreMethImpl = nbreMethImpl;
	}
	public   void setPctMethPub(float pctMethPub) {
		PctMethPub = pctMethPub;
	}
	public   void setPctMethHer(float pctMethHer) {
		PctMethHer = pctMethHer;
	}
	public   void setDIT(int dIT) {
		DIT = dIT;
	}
	public   void setNOC(Map<String, Integer> nocmap2) {
		NOCmap = nocmap2;
		for (String mapKey : NOCmap.keySet()) {
			
			if(mapKey==nomClasse)
				NOC= NOCmap.get(mapKey);
		}
	}
	public   void setWMC(int wMC) {
		WMC = wMC;
	}
	public   void setLCOM(int lCOM) {
		LCOM = lCOM;
	}
	public   void setCBO(int cBO) {
		CBO = cBO;
	}
	public   void setFanOut(int fanOut1) {
		fanOut = fanOut1;
	}
	public   void setFanIn(int fanIn1) {
		fanIn = fanIn1;
	}

}
