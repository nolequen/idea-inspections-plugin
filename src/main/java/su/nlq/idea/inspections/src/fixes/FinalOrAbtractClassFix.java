package fixes;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import org.jetbrains.annotations.NotNull;

public final class FinalOrAbtractClassFix implements LocalQuickFix {

  @Override
  @NotNull
  public String getFamilyName() {
    return "Add class modifier";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    final PsiElement element = descriptor.getPsiElement();
    if (!(element instanceof PsiClass)) {
      return;
    }
    final PsiClass psiClass = (PsiClass) element;
    final PsiModifierList modifiers = psiClass.getModifierList();
    if (modifiers == null) {
      return;
    }

    final String keyword = ClassInheritorsSearch.search(psiClass).findFirst() == null
        ? PsiKeyword.FINAL
        : PsiKeyword.ABSTRACT;

    modifiers.add(JavaPsiFacade.getElementFactory(project).createKeyword(keyword));
  }
}