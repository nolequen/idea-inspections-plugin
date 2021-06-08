package su.nlq.idea.inspections;

import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface AnnotationsOwner {
  @NotNull
  AnnotationsOwner empty = annotation -> false;

  @SuppressWarnings("StaticMethodNamingConvention")
  @NotNull
  static AnnotationsOwner of(@NotNull PsiModifierListOwner element) {
    return Optional.ofNullable(element.getModifierList()).map(modifiers -> (AnnotationsOwner) new Wrapper(modifiers)).orElse(empty);
  }

  boolean has(@NotNull Class<? extends Annotation> annotation);

  final class Wrapper implements AnnotationsOwner {
    @NotNull
    private final PsiModifierList modifiers;

    private Wrapper(@NotNull PsiModifierList modifiers) {
      this.modifiers = modifiers;
    }

    @Override
    public boolean has(@NotNull Class<? extends Annotation> annotation) {
      return modifiers.findAnnotation(annotation.getCanonicalName()) != null;
    }
  }
}
