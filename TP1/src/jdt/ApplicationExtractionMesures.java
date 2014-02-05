package jdt;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class ApplicationExtractionMesures implements IApplication {

	private IJavaModel javaModel;

	@Override
	public Object start(IApplicationContext context) throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		javaModel = JavaCore.create(root);
		javaModel.open(new NullProgressMonitor());
		IProject[] projects = root.getProjects();

		for (IProject project : projects) {
			processProject(project);
		}
		javaModel.close();
		return null;
	}

	private void processProject(IProject project) throws JavaModelException {
		if (!project.exists()) {
			return;
		}
		IJavaProject javaProject = javaModel.getJavaProject(project.getName());
		
		IPackageFragmentRoot[] roots = javaProject.getAllPackageFragmentRoots();
		for (IPackageFragmentRoot root : roots) {
			root.open(new NullProgressMonitor());
			switch (root.getElementType()) {
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				IJavaElement[] children = root.getChildren();
				for (IJavaElement javaElems : children) {
					if (!(javaElems instanceof IPackageFragment)) {
						continue;
					}
					IPackageFragment fragment = (IPackageFragment) javaElems;
					ICompilationUnit[] compilationUnits = fragment
							.getCompilationUnits();
					for (ICompilationUnit compUnit : compilationUnits) {
						ASTParser parser = ASTParser.newParser(AST.JLS4);
						parser.setProject(javaProject);
						parser.setResolveBindings(true);
						parser.setBindingsRecovery(true);
						parser.setSource(compUnit);
						ASTNode rootNode = parser
								.createAST(new NullProgressMonitor());
						ASTVisitor visitor = new GenerateurMesures();
						rootNode.accept(visitor);
					}
				}

			default:
			}
			root.close();
		}
	}

	@Override
	public void stop() {
		try {
			javaModel.close();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

}
