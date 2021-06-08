package su.nlq.idea.inspections;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.codeInspection.util.SpecialAnnotationsUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public final class AnnotatedInspection extends AbstractBaseJavaLocalInspectionTool {

  @SuppressWarnings("PublicField")
  public @NotNull List<String> annotation = new ArrayList<>();

  @Override
  @NotNull
  public JComponent createOptionsPanel() {
    final MultipleCheckboxOptionsPanel panel = new MultipleCheckboxOptionsPanel(this);
    panel.add(SpecialAnnotationsUtil.createSpecialAnnotationsListControl(annotation, "Report annotations:"), "growx, wrap");
    return panel;
  }

  @Override
  @NotNull
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problems, boolean isOnTheFly) {
    return new JavaElementVisitor() {

      @Override
      public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expression) {
        final PsiElement element = expression.getMethodExpression();
        final PsiMethod member = expression.resolveMethod();
        if (!valid(element, member)) {
          problems.registerProblem(element, "Annotated with @" + annotation + " method is called from not annotated code");
        }
      }

      @Override
      public void visitNewExpression(@NotNull PsiNewExpression expression) {
        PsiJavaCodeReferenceElement reference = expression.getClassOrAnonymousClassReference();
        if (reference == null) {
          return;
        }
        final PsiMethod method = expression.resolveMethod();
        if (!valid(reference, method)) {
          problems.registerProblem(reference, "Annotated with @" + annotation + " method is called from not annotated code", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
          return;
        }
        final PsiMember member = ObjectUtils.tryCast(reference.resolve(), PsiMember.class);
        if (!valid(reference, member)) {
          problems.registerProblem(reference, "Annotated with @" + annotation + " field is referenced from not annotated code", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        }
      }

      @Override
      public void visitMethodReferenceExpression(@NotNull PsiMethodReferenceExpression expression) {
        final PsiElement element = expression.resolve();
        if (element instanceof PsiMethod) {
          final PsiMethod method = (PsiMethod) element;
          if (!valid(expression, method)) {
            problems.registerProblem(expression, "Annotated with @" + annotation + " method is called from not annotated code", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
          }
        }
      }

      @Override
      public void visitReferenceExpression(@NotNull PsiReferenceExpression reference) {
        final PsiElement element = reference.resolve();
        if (element instanceof PsiField) {
          final PsiField field = (PsiField) element;
          if (!valid(reference, field)) {
            problems.registerProblem(reference, "Annotated with @" + annotation + " field is referenced from not annotated code", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
          }
        }
      }

      @Override
      public void visitReferenceElement(@NotNull PsiJavaCodeReferenceElement reference) {
        if (reference.getParent() instanceof PsiNewExpression) {
          return;
        }
        if (reference.getParent() instanceof PsiAnonymousClass) {
          return;
        }
        if (PsiTreeUtil.getParentOfType(reference, PsiImportStatementBase.class) != null) {
          return;
        }
        final PsiElement element = reference.resolve();
        if (element instanceof PsiClass) {
          final PsiClass clazz = (PsiClass) element;
          if (!valid(reference, clazz)) {
            problems.registerProblem(reference, "Annotated with @" + annotation + " class is referenced from not annotated code", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
          }
        }
      }
    };
  }

  private static @Nullable <T extends PsiElement> T topLevelParent(@Nullable PsiElement element, @NotNull Class<T> type) {
    T parent = PsiTreeUtil.getParentOfType(element, type);
    if (parent == null) {
      return null;
    }
    do {
      final T next = PsiTreeUtil.getParentOfType(parent, type);
      if (next == null) {
        return parent;
      }
      parent = next;
    }
    while (true);
  }

  private boolean valid(@NotNull PsiElement element, @Nullable PsiMember member) {
    if (member == null) {
      return true;
    }
    if (!annotated(member)) {
      return true;
    }
    if (annotated(topLevelParent(element, PsiMethod.class))) {
      return true;
    }
    if (annotated(topLevelParent(element, PsiField.class))) {
      return true;
    }
    return annotated(topLevelParent(element, PsiClass.class));
  }

  private boolean annotated(@Nullable PsiMember member) {
    if (member == null) {
      return false;
    }
    if (AnnotationUtil.isAnnotated(member, annotation, AnnotationUtil.CHECK_EXTERNAL)) {
      return true;
    }
    return annotated(member.getContainingClass());
  }
}
