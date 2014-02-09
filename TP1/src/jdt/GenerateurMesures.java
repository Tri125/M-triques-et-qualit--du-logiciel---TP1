package jdt;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

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
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * Visiteur de l'arbre de synthaxe Java fourni par eclipse 3.8 et 4.3
 * 
 * @author vauchers@iro.umontreal.ca
 */



final class GenerateurMesures extends ASTVisitor {
	
	private int wmc_ComplexiteCyclomatic;
	
	
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

	@Override
	
	public boolean visit(TypeDeclaration type) {
		System.out.println("Traitement Classe/Interface: "
				+ type.getName().getFullyQualifiedName());
		
		//System.out.println("Nombre de Ligne: " + (type.getl);
		// Possible de trouver les supertypes Ã  partir de la dÃ©claration du type

		// Si l'arbre d'hÃ©ritage est connu
        ITypeBinding typeBind = type.resolveBinding();      

		// Pour la taille, il est possible d'utiliser de trouver la ligne Ã 
		// partir de la mÃ©thode:
		// compilationUnit.getLineNumber(position);
		CompilationUnit cmpU = (CompilationUnit)(type.getRoot());
		
	
		if (!type.isInterface()) //Donc une classe
		{
			Set<String> setInterfaces = new HashSet<String>();
			extractLOC(type, cmpU);
			extractPackageName(cmpU);
			NbrImplementedAttribute(typeBind.getDeclaredFields());
			InterFaceNumber(setInterfaces, typeBind, cmpU.getAST());
			PublicAttrPourcent(typeBind.getDeclaredFields());
			InheritedAttrPourcent(typeBind.getDeclaredFields().length, 0, 0, typeBind, cmpU.getAST());
			DIT(typeBind,cmpU.getAST(),0);
		}
		return super.visit(type);
	}
	
	
	public void InheritedAttrPourcent(final int nbrAttrSource, int nbrAttrInherit, int profondeur, ITypeBinding typeBind, AST ast)
	{
        ITypeBinding superTypeBind = typeBind.getSuperclass();
        
        if (profondeur == 0) //On skip à la profondeur 0 car nous cherchons les attributs hérités, pas ceux que la classe offre
        {
        	InheritedAttrPourcent(nbrAttrSource, nbrAttrInherit, ++profondeur, superTypeBind, ast);
        }
        else
        {
        	if (!typeBind.isEqualTo(ast.resolveWellKnownType("java.lang.Object"))) //N'est pas la première classe, donc nous pouvons commencer à vérifier les attributs protected (qui sont hérités)
        	{
        		for (IVariableBinding t : typeBind.getDeclaredFields() )
            	{
        			int modifier = t.getModifiers();
            		if (Flags.isProtected(modifier))
            		{
            			System.out.println("PROTECTED FIELD : " + t.getName());
            			nbrAttrInherit++;
            		}
            	}
            	InheritedAttrPourcent(nbrAttrSource, nbrAttrInherit, ++profondeur, superTypeBind, ast);
            }
        	else
        	{
    			//System.out.println(nbrAttrSource );
            	float pourcent = 0.0f;
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                    
                if (nbrAttrSource != 0)
                {
                  pourcent = ((float)nbrAttrInherit /(nbrAttrSource + nbrAttrInherit) ) * 100;
                }
                System.out.println("Pourcentage Attribut Heriter : " + df.format(pourcent) + "%");
                System.out.println("Nombres d'attributs Heriter: " + nbrAttrInherit);
        	}
        	
        }
    
	}
	
	public void DIT(ITypeBinding typeBind, AST ast, int depth)
	{  
        ITypeBinding superTypeBind = typeBind.getSuperclass();
        
        if (!superTypeBind.isEqualTo(ast.resolveWellKnownType("java.lang.Object")))
        {
        	DIT(superTypeBind, ast, ++depth);
        }
        else
        {
        	System.out.println("Profondeur dans l'arbre d'heritage: " + depth);
        }
		
	}
	
	public void InterFaceNumber(Set<String> setInterfaces, ITypeBinding typeBind, AST ast)
	{
        ITypeBinding[] interfaceBinds = typeBind.getInterfaces(); 
        for (ITypeBinding t : interfaceBinds)
        {
        	setInterfaces.add(t.getQualifiedName());
        }
        
        ITypeBinding superTypeBind = typeBind.getSuperclass();
        
        if (!typeBind.isEqualTo(ast.resolveWellKnownType("java.lang.Object")))
        {
        	InterFaceNumber(setInterfaces, superTypeBind, ast);
        }
        else
        {
            for (String interf : setInterfaces)
            {
                System.out.println("INTERFACE: " + interf);
            }
        }
	}
	
	public void NbrImplementedAttribute(IVariableBinding[] varBinding)
	{
        for (IVariableBinding t : varBinding )
        {
        	int modifier = t.getModifiers();
        	if(Flags.isFinal(modifier)) //Les constantes ne sont pas considérés comme un attribut FINAL a un flag de valeur "16"
        	{
        		System.out.println("Constante declarer : " + t.getName());
        	}
        	else
            	System.out.println("Attribut declarer : " + t.getName());
        } 
	}
	
	public void PublicAttrPourcent(IVariableBinding[] varBinding)
	{
		int nbrPublic = 0;
		//int nbrPrivate = 0;
		float pourcent = 0.0f;
		
		
        for (IVariableBinding t : varBinding )
        {
        	int modifier = t.getModifiers();
        	if (Flags.isPublic(modifier))
        	{
        		nbrPublic++;
        	}
        } 
        if (varBinding.length != 0)
        {
        	pourcent = ((float)nbrPublic /varBinding.length) * 100;
        }
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
    	System.out.println("Pourcentage Attribut Publique : " + df.format(pourcent) + "%");
	}
	
	public void extractLOC(TypeDeclaration type, CompilationUnit cmpU)
	{
		int nodeLength = type.getLength();
		int endLineNumber = cmpU.getLineNumber(type.getStartPosition() + nodeLength);
		System.out.println("Nombre de Ligne : " + endLineNumber); //LOC
	}
	
	public void extractPackageName(CompilationUnit cmpU)
	{
		String nomPackage;
		if (cmpU.getPackage() == null)
		{
			nomPackage = "Default";
		}
		else
			nomPackage = cmpU.getPackage().toString();
		System.out.println("Package: " + nomPackage);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println("Traitement Methode: "
				+ node.getName().getFullyQualifiedName());
		
		wmc_ComplexiteCyclomatic = 1;
		return super.visit(node);
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {
		System.out.println("WMC Complexité Cyclomatique de la methode " + node.getName().getFullyQualifiedName() + "\t: "
		+ wmc_ComplexiteCyclomatic);
	}
	
	
	
	public boolean visit (IfStatement node) {
		wmc_ComplexiteCyclomatic++;
		return true;
	}
	
	public boolean visit (ForStatement node) {
		wmc_ComplexiteCyclomatic++;
		return true;
	}
	
	public boolean visit (WhileStatement node) {
		wmc_ComplexiteCyclomatic++;
		return true;
	}
	
	public boolean visit (SwitchCase node) {
		wmc_ComplexiteCyclomatic++;
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