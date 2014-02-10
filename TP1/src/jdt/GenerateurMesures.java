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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.Modifier;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * Visiteur de l'arbre de synthaxe Java fourni par eclipse 3.8 et 4.3
 * 
 * @author vauchers@iro.umontreal.ca
 */



final class GenerateurMesures extends ASTVisitor {
	
	private int wmc_ComplexiteCyclomatic;
	private static Map <String, Metrique> workSpaceMetric = new HashMap<String, Metrique>(); //Les resultats de tout le workspace
	private int nbremthimp=0;
	private int nbreattrimp=0;
	private int mthpublic=0;
	private int mthnonpublic=0;
	private int mthherite=0;
	private int mthnonherite=0;
	private int nbreinter=0;
	private float pourcentAttrHerite = 0.0f;
	private int profondeurArbre = 0;
	private float pourcentAttrPublic = 0.0f;
	private int totalLines = 0;
	private static Map <String,Integer> noc = new HashMap<String,Integer>();
	private Map <String, Integer> coupling = new HashMap<String, Integer>();
	
	Metrique metrique = new Metrique();
	
	
	
	
	@Override
	public void endVisit(TypeDeclaration type)
	{
		//affichage sur la console du nombre de methodes implementées a la fin de la visite de la classe
		//System.out.println("Nombre de methodes implementees "+nbremthimp);

		
		float pourcentpublique=(float) ((mthpublic*100.00)/(mthpublic+mthnonpublic));
		float pourcentherite=(float) ((mthherite*100.00)/(mthherite+mthnonherite));
		
		//affichage sur la console du pourcentage  de methodes publiques a la fin de la visite de la classe
		//System.out.println("Pourcentage methodes publiques "+pourcentpublique+"%");
		
		//affichage sur la console du pourcentage  de methodes héritées a la fin de la visite de la classe

		//System.out.println("Pourcentage methodes heritees "+pourcentherite+"%");
		//remise a zero des variables pour les tests lors de la visite des autres classes.

		
		//for (Entry<String, Integer> val : coupling.entrySet()) {
		   // System.out.println(val.getKey() +"\t" + val.getValue());
		//}
		//affichage sur la console du fanout
	    //System.out.println("FanOut: " + coupling.size());
		
		metrique.setNbreMethImpl(nbremthimp);
        metrique.setNbreAttrImpl(nbreattrimp);
        metrique.setNOC(noc);
	    metrique.setFanOut(coupling.size());
		metrique.setPctMethHer(pourcentherite);
		metrique.setPctMethPub(pourcentpublique);
		metrique.setWMC(wmc_ComplexiteCyclomatic);
		metrique.setNomClasse(extractName(type, (CompilationUnit)(type.getRoot())));
        metrique.setPctAttrHer(pourcentAttrHerite);
    	metrique.setDIT(profondeurArbre);
        metrique.setNbreInterfaces(nbreinter);
    	metrique.setPctAttrPub(pourcentAttrPublic);
		metrique.setLOC(totalLines);
		
		workSpaceMetric.put(extractName(type, (CompilationUnit)(type.getRoot())), metrique);
        
	    //System.out.println(metrique.toString());
	    
	    
	}
	
	public static void Resultat()
	{
		for (Metrique val : workSpaceMetric.values()) {
			System.out.println();
		    System.out.println(val.toString());
		}
	}
	
	@Override
	public void endVisit(MethodInvocation node) {
		IMethodBinding methodBinding = node.resolveMethodBinding();
		// Si l'implantation d'une mÃ©thode est connue, il y aura un method
		// binding
		
		if (methodBinding != null) {
			ITypeBinding declaringClass = methodBinding.getDeclaringClass();
			ITypeBinding[] parameterTypes = methodBinding.getParameterTypes();
		} else {
			// Pas de dÃ©finition connue.
		}
		super.endVisit(node);
	}

	
	public boolean visit(TypeDeclaration type) {
		
		System.out.println("Traitement Classe/Interface: "
				+ type.getName().getFullyQualifiedName());
		// Si l'arbre d'hÃ©ritage est connu
        ITypeBinding typeBind = type.resolveBinding();      

		// Pour la taille, il est possible d'utiliser de trouver la ligne Ã 
		// partir de la mÃ©thode:
		// compilationUnit.getLineNumber(position);
		CompilationUnit cmpU = (CompilationUnit)(type.getRoot());
		
	
		if (!type.isInterface()) //Si le type est une classe
		{
			Set<String> setInterfaces = new HashSet<String>();
			
			//on va  extraire le nombre de lignes
			extractLOC(type, cmpU);
			//on va  aussi chercher le nombre d'attributs implantés
			NbrImplementedAttribute(typeBind.getDeclaredFields());
			//on va chercher  les interfaces de la classe toutes transitivement heritees
			InterFaceNumber(setInterfaces, typeBind, cmpU.getAST());
			//calcul du pourcentage des attributs publiques
			PublicAttrPourcent(typeBind.getDeclaredFields());
			//calcul du pourcentage des attributs herites
			InheritedAttrPourcent(typeBind.getDeclaredFields().length, 0, 0, typeBind, cmpU.getAST());
			//Recherche de la profondeur de l'arbre d'heritage
			DIT(typeBind,cmpU.getAST(),0);
			
	        for (IVariableBinding t : typeBind.getDeclaredFields() )
	        {
	        	if (!t.getType().isPrimitive())
	        	{
	        		if (!coupling.containsKey(t.getType().getName()))
	        		{
	        			coupling.put(t.getType().getName(), 1);
	        		}
	        	}
	        } 
			
	        
	        // Pour trouver le nombre de fils d'une classe
	        /*Pour cela on va contenir les informations dans une map avec comme cle le pere
	        et comme valeur le nombre de fils*/
	        if(type.getSuperclassType() != null&&!type.isInterface())
	        {
	        	String pere =type.getSuperclassType().toString();
	       
	        	if (!noc.containsKey(pere))
	        		noc.put(pere,1);
	        	else 
	        		noc.put(pere,noc.get(pere)+1);

	        }
		}
		return super.visit(type);
	}
	
	//le travail sur cette methode est inacheve d'ou son implémentation
	private void CBO()
	{
		
	}
	
	
	
	private void InheritedAttrPourcent(final int nbrAttrSource, int nbrAttrInherit, int profondeur, ITypeBinding typeBind, AST ast)
	{
        ITypeBinding superTypeBind = typeBind.getSuperclass();
        
        if (profondeur == 0) //On skip à la profondeur 0 car nous cherchons les attributs hérités, pas ceux que la classe offre
        {
        	InheritedAttrPourcent(nbrAttrSource, nbrAttrInherit, ++profondeur, superTypeBind, ast);
        }
        else
        {
        	//N'est pas la première classe, donc nous pouvons commencer à vérifier les attributs protected (qui sont hérités)
        	//Exclusion de la classe Object de l'évaluation
        	if (!typeBind.isEqualTo(ast.resolveWellKnownType("java.lang.Object"))) 
        	{
        		for (IVariableBinding t : typeBind.getDeclaredFields() )
            	{
        			int modifier = t.getModifiers();
            		if (Flags.isProtected(modifier))
            		{
            			//System.out.println("PROTECTED FIELD : " + t.getName());
            			nbrAttrInherit++;
            		}
            	}
            	InheritedAttrPourcent(nbrAttrSource, nbrAttrInherit, ++profondeur, superTypeBind, ast);
            }
        	else
        	{
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                    
                if (nbrAttrSource != 0)
                {
                	pourcentAttrHerite = ((float)nbrAttrInherit /(nbrAttrSource + nbrAttrInherit) ) * 100;
                }
                
                //Affichage sur la console 
                //System.out.println("Nombres d'attributs Heriter: " + nbrAttrInherit);
                //System.out.println("Nombres d'attributs de la classe evaluer: " + nbrAttrSource);
                //System.out.println("Pourcentage Attribut Heriter : " + df.format(pourcentAttrHerite) + "%");
        	}
        	
        }
    
	}
	
	private void DIT(ITypeBinding typeBind, AST ast, int depth)
	{  
        ITypeBinding superTypeBind = typeBind.getSuperclass();
        
        //On cesse notre évaluation avant d'atteindre la classe Object
        if (!superTypeBind.isEqualTo(ast.resolveWellKnownType("java.lang.Object")))
        {
        	DIT(superTypeBind, ast, ++depth);
        }
        else
        {
        	//Affichage sur la console de la Profondeur dans l'arbre d'heritage
        	//System.out.println("Profondeur dans l'arbre d'heritage: " + depth);
        	profondeurArbre = depth;
        }
		
	}
	
	private void InterFaceNumber(Set<String> setInterfaces, ITypeBinding typeBind, AST ast)
	{
        ITypeBinding[] interfaceBinds = typeBind.getInterfaces(); 
        for (ITypeBinding t : interfaceBinds)
        {
        	//Utilisation de Set pour ne pas compter plus d'une fois la même interface.
        	setInterfaces.add(t.getQualifiedName());
        }
        
        ITypeBinding superTypeBind = typeBind.getSuperclass();
        
        //On cesse notre évaluation avant d'atteindre la classe Object
        if (!typeBind.isEqualTo(ast.resolveWellKnownType("java.lang.Object")))
        {
        	InterFaceNumber(setInterfaces, superTypeBind, ast);
        }
        else
        {
        	//Affichage sur la console des interfaces toutes transitivement héritées

            for (String interf : setInterfaces)
            {
                //System.out.println("INTERFACE: " + interf);
            	//Augmente le compte pour chaque interfaces présentent dans le Set.
                nbreinter++;
            }
            
            //System.out.println(" Nombre INTERFACE: " + nbreinter);
        }
	}
	
	private void NbrImplementedAttribute(IVariableBinding[] varBinding)
	{
        for (IVariableBinding t : varBinding )
        {
        	int modifier = t.getModifiers();
        	if(Flags.isFinal(modifier)) //Les constantes ne sont pas considérés comme un attribut.
        	{
        		//utilisé pour verifier nos resultats
        		//System.out.println("Constante declarer : " + t.getName());
        	}
        	else //N'est pas une constante, donc un attribut
        	{
        		nbreattrimp++;
        		//utilisé pour verifier nos resultats
            	//System.out.println("Attribut declarer : " + t.getName());
        
        	}
        }
        //Affichage sur la console du nombre d'attributs implemanter
         //System.out.println("Attribut declarer : "+nbreattrimp);
        
	}
	
	//pourcentage attributs publiques
	private void PublicAttrPourcent(IVariableBinding[] varBinding)
	{
		int nbrPublic = 0;
		
		
        for (IVariableBinding t : varBinding )
        {
        	int modifier = t.getModifiers();
        	if (Flags.isPublic(modifier)) //Si l'attribut est publique
        	{
        		nbrPublic++;
        		//System.out.println("Attribut Publique :" + t.getName());
        	}
        } 
        if (varBinding.length != 0)
        {
        	pourcentAttrPublic = ((float)nbrPublic /varBinding.length) * 100;
        }
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        
        //affichage sur la console du pourcentage
    	//System.out.println("Pourcentage Attribut Publique : " + df.format(pourcentAttrPublic) + "%");
	}
	
	
	//calcul du nombre de lignes
	private void extractLOC(TypeDeclaration type, CompilationUnit cmpU)
	{
		int nodeLength = type.getLength();
		int endLineNumber = cmpU.getLineNumber(type.getStartPosition() + nodeLength);
		
		totalLines = endLineNumber;
		//affichage sur la console du nombre de ligne
		//System.out.println("Nombre de Ligne : " + totalLines);
	}
	
	//extraction du nom de package et de classe
	private String extractName(TypeDeclaration type, CompilationUnit cmpU)
	{
		String nomPackage;
		if (cmpU.getPackage() == null)
		{
			nomPackage = "Default";
		}
		else
		{
			nomPackage = cmpU.getPackage().toString();
			nomPackage = nomPackage.substring(8);
			nomPackage = nomPackage.replace(";\n", "");
			
		}
		//affichage sur la console du nom
		//System.out.println("Nom: " + nomPackage + "." + type.getName().getFullyQualifiedName());
		return nomPackage + "." + type.getName().getFullyQualifiedName();
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println("Traitement Methode: "
				+ node.getName().getFullyQualifiedName());
		
		//pour le nombre de méthodes implémentées
		nbremthimp+=1;
	    
		int i = node.getModifiers();
	    
	    String methodetemp = Modifier.toString(i);
	    
	  //pour le pourcentage des methodes publiques
	    if(methodetemp.contains("public"))
			mthpublic+=1;
		else
			mthnonpublic+=1;
	  //pour le pourcentage des methodes heritees
		if(methodetemp.contains("super"))
			mthherite+=1;
		else
			mthnonherite+=1;
		
		//pour le wmc Chaque methode a une complexite de depart de 1
		wmc_ComplexiteCyclomatic += 1;
		
		return super.visit(node);
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {
		
		//Extraction du type des parametres de la methode.
		//ERREUR: Nous n'avons pas reussis a filtrer les types indesirables comme les collections java, String et les typePrimitif.
		//L'erreur influence considerablement le calcul de CBO et de fanOut
		List<String> parameters = new ArrayList<String>();
        for (Object parameter : node.parameters()) {
            VariableDeclaration variableDeclaration = (VariableDeclaration) parameter;
            String type = variableDeclaration.getStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY)
                    .toString();
            for (int i = 0; i < variableDeclaration.getExtraDimensions(); i++) {
                type += "[]";
            }
            parameters.add(type);
        }
        for (String val : parameters)
        {
        
        	if (val != "char" && val != "String" && val != "int")
        	{
        		if (!coupling.containsKey(val))
        		{
        			coupling.put(val, 1);
        		}
        	}
        	
        }

	}
	
	
	//Pour chaque embranchement de if, on rajoute une complexite WMC
	public boolean visit (IfStatement node) {
		wmc_ComplexiteCyclomatic++;
		return true;
	}
	
	//Pour chaque embranchement de for, on rajoute une complexite WMC
	public boolean visit (ForStatement node) {
		wmc_ComplexiteCyclomatic++;
		return true;
	}
	
	//Pour chaque embranchement de while, on rajoute une complexite WMC
	public boolean visit (WhileStatement node) {
		wmc_ComplexiteCyclomatic++;
		return true;
	}
	
	//Pour chaque embranchement de case, on rajoute une complexite WMC
	public boolean visit (SwitchCase node) {
		wmc_ComplexiteCyclomatic++;
		return true;
	}
	
	
	//Pour le Couplage, rajoute chaque variable declarer qui n'est pas de type primitif dans une map.
	//ERREUR: Ne filtre pas les types indesirables comme les collections java et les String.
	public boolean visit (VariableDeclarationStatement node) {
    	if (!node.getType().isPrimitiveType())
    	{
    		if (!coupling.containsKey(node.getType().toString()))
    		{
    			coupling.put(node.getType().toString(), 1);
    		}
    	}
		return true;
	}
	

	@Override
	public boolean visit(CompilationUnit node) {
	
		return super.visit(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		super.endVisit(node);
	}
	
	

}

