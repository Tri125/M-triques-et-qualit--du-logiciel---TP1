package jdt;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Visiteur de l'arbre de synthaxe Java fourni par eclipse 3.8 et 4.3
 * 
 * @author vauchers@iro.umontreal.ca
 */
final class GenerateurMesures extends ASTVisitor {
	@Override
	public void endVisit(MethodInvocation node) {
		IMethodBinding methodBinding = node.resolveMethodBinding();
		// Si l'implantation d'une méthode est connue, il y aura un method
		// binding
		if (methodBinding != null) {
			ITypeBinding declaringClass = methodBinding.getDeclaringClass();
			ITypeBinding[] parameterTypes = methodBinding.getParameterTypes();
		} else {
			// Pas de définition connue.
		}
		super.endVisit(node);
	}

	@Override
	
	public boolean visit(TypeDeclaration type) {
		System.out.println("Traitement Classe/Interface: "
				+ type.getName().getFullyQualifiedName());
		
		//System.out.println("Nombre de Ligne: " + (type.getl);
		// Possible de trouver les supertypes à partir de la déclaration du type

		// Si l'arbre d'héritage est connu
		//ITypeBinding typeBinding = type.resolveBinding();
		
		//typeBinding.getSuperclass();
		//typeBinding.getInterfaces();
		// Pour la taille, il est possible d'utiliser de trouver la ligne à
		// partir de la méthode:
		// compilationUnit.getLineNumber(position);
		CompilationUnit cmpU = (CompilationUnit)(type.getRoot());
		int StartlineNumber = cmpU.getLineNumber(type.getStartPosition()) -1;
		int nodeLength = type.getLength();
		System.out.println("Package: " + cmpU.getPackage());
		int endLineNumber = cmpU.getLineNumber(type.getStartPosition() + nodeLength) - 1;
		System.out.println("Nombre de Ligne : " + endLineNumber);
		return super.visit(type);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println("Traitement Methode: "
				+ node.getName().getFullyQualifiedName());
		return super.visit(node);
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