package su.nlq.idea.inspections.src.fixes;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public final class InsertAnnotationFix implements LocalQuickFix {
  @NotNull
  private final String annotationText;

  public InsertAnnotationFix(@NotNull Class<?> annotationClass) {
    this.annotationText = '@' + annotationClass.getSimpleName();
  }

  @Override
  @NotNull
  public String getName() {
    return "Insert " + annotationText + " annotation";
  }

  @Override
  @NotNull
  public String getFamilyName() {
    return "Insert annotation";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    final PsiElement element = descriptor.getPsiElement();
    if (!(element instanceof PsiModifierListOwner)) {
      return;
    }
    final PsiModifierList modifiers = ((PsiModifierListOwner) element).getModifierList();
    if (modifiers != null) {
      modifiers.addBefore(createAnnotation(project, element), modifiers.getFirstChild());
    }
  }

  @NotNull
  private PsiAnnotation createAnnotation(@NotNull Project project, @NotNull PsiElement element) {
    return JavaPsiFacade.getElementFactory(project).createAnnotationFromText(annotationText, element);
  }
}
