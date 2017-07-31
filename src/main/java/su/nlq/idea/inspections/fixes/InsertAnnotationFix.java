package su.nlq.idea.inspections.fixes;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public final class InsertAnnotationFix implements LocalQuickFix {
  @NotNull
  private final String annotationText;
  @NotNull
  private final InsertionPlace place;

  public InsertAnnotationFix(@NotNull Class<?> annotationClass, boolean insertFirst) {
    this.annotationText = '@' + annotationClass.getSimpleName();
    this.place = insertFirst ? InsertionPlace.First : InsertionPlace.Last;
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
    if (modifiers == null) {
      return;
    }
    final PsiAnnotation annotation = JavaPsiFacade.getElementFactory(project).createAnnotationFromText(annotationText, element);
    place.insert(modifiers, annotation);
  }

  private enum InsertionPlace {
    First {
      @Override
      public void insert(@NotNull PsiModifierList modifiers, @NotNull PsiAnnotation annotation) {
        modifiers.addBefore(annotation, modifiers.getFirstChild());
      }
    },

    Last {
      @Override
      public void insert(@NotNull PsiModifierList modifiers, @NotNull PsiAnnotation annotation) {
        modifiers.addAfter(annotation, modifiers.getLastChild());
      }
    };

    public abstract void insert(@NotNull PsiModifierList modifiers, @NotNull PsiAnnotation annotation);
  }
}
